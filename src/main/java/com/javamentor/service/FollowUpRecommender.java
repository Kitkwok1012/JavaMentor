package com.javamentor.service;

import com.javamentor.entity.Question;
import com.javamentor.entity.UserProgress;
import com.javamentor.repository.QuestionRepository;
import com.javamentor.repository.UserProgressRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Follow-up Question Recommender
 * Implements 7-rule algorithm for intelligent question recommendation
 */
@Service
public class FollowUpRecommender {
    
    private static final Logger log = LoggerFactory.getLogger(FollowUpRecommender.class);
    
    private final QuestionRepository questionRepository;
    private final UserProgressRepository userProgressRepository;
    
    public FollowUpRecommender(QuestionRepository questionRepository, 
                               UserProgressRepository userProgressRepository) {
        this.questionRepository = questionRepository;
        this.userProgressRepository = userProgressRepository;
    }
    
    /**
     * Recommend next question based on answer result
     * Rules (applied in order):
     * 1. IF wrong → find questions with same subtopic + same or lower difficulty
     * 2. IF correct + difficulty == easy → escalate to medium
     * 3. IF correct + difficulty == medium → escalate to hard OR jump to related subtopic
     * 4. IF user has answered same subtopic ≥ 3 times recently → inject different topic
     * 5. IF user's weak topics (< 60% accuracy) → periodically re-inject every 10 questions
     * 6. NEVER repeat a question within the same session
     * 7. Prioritize unanswered questions over seen ones
     */
    public Question recommend(Long currentQuestionId, boolean correct) {
        Question current = getQuestionById(currentQuestionId);
        if (current == null) return getRandomQuestion(new HashSet<>());
        
        // Get all progress history
        List<UserProgress> history = userProgressRepository.findAllByOrderByAnsweredAtDesc();
        
        // Get questions already answered
        Set<Long> answeredIds = history.stream()
            .map(p -> p.getQuestion().getId())
            .collect(Collectors.toSet());
        answeredIds.add(currentQuestionId);
        
        // Get user's weak topics (Rule 5)
        Map<String, Double> topicAccuracy = calculateTopicAccuracy(history);
        List<String> weakTopics = topicAccuracy.entrySet().stream()
            .filter(e -> e.getValue() < 0.6)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
        
        // Count recent answers for current topic (Rule 4)
        long recentCountInTopic = history.stream()
            .filter(p -> p.getQuestion().getTopic().getTopicId().equals(current.getTopic().getTopicId()))
            .count();
        
        // Rule 1: Wrong answer → same subtopic, same or lower difficulty
        if (!correct) {
            Question q = findQuestionSameTopic(
                current.getTopic().getTopicId(), 
                current.getDifficulty(),
                answeredIds,
                true // same or lower difficulty
            );
            if (q != null) return q;
        }
        
        // Rule 4: Too many recent answers in same topic → inject different topic
        if (recentCountInTopic >= 3 && !weakTopics.isEmpty()) {
            Question q = findQuestionFromTopics(weakTopics, answeredIds);
            if (q != null) return q;
        }
        
        // Rule 5: Weak topics → inject weak topic question (every 10 questions)
        if (history.size() % 10 == 0 && !weakTopics.isEmpty()) {
            Question q = findQuestionFromTopics(weakTopics, answeredIds);
            if (q != null) return q;
        }
        
        // Rule 2-3: Correct answer → escalate difficulty
        if (correct) {
            if (current.getDifficulty() == 1) {
                // Easy → Medium
                Question q = findQuestionSameTopic(
                    current.getTopic().getTopicId(),
                    2, // medium
                    answeredIds,
                    false
                );
                if (q != null) return q;
            } else if (current.getDifficulty() == 2) {
                // Medium → Hard or related topic
                Question q = findQuestionSameTopic(
                    current.getTopic().getTopicId(),
                    3, // hard
                    answeredIds,
                    false
                );
                if (q != null) return q;
            }
        }
        
        // Rule 7: Prioritize unanswered questions
        Question q = getRandomUnansweredWithExclude(answeredIds);
        if (q != null) return q;
        
        // Fallback: random question not in exclude list
        return getRandomQuestion(answeredIds);
    }
    
    @Cacheable(value = "questions", key = "#questionId")
    public Question getQuestionById(Long questionId) {
        log.debug("Fetching question from DB: {}", questionId);
        return questionRepository.findById(questionId).orElse(null);
    }
    
    private Map<String, Double> calculateTopicAccuracy(List<UserProgress> history) {
        Map<String, List<Boolean>> topicResults = new HashMap<>();
        
        for (UserProgress p : history) {
            String topicId = p.getQuestion().getTopic().getTopicId();
            topicResults.computeIfAbsent(topicId, k -> new ArrayList<>()).add(p.getIsCorrect());
        }
        
        Map<String, Double> accuracy = new HashMap<>();
        for (Map.Entry<String, List<Boolean>> entry : topicResults.entrySet()) {
            long correct = entry.getValue().stream().filter(Boolean::booleanValue).count();
            accuracy.put(entry.getKey(), (double) correct / entry.getValue().size());
        }
        
        return accuracy;
    }
    
    private Question findQuestionSameTopic(String topicId, int maxDifficulty, 
                                          Set<Long> excludeIds, boolean allowLowerDifficulty) {
        List<Question> candidates;
        
        if (allowLowerDifficulty) {
            candidates = questionRepository.findByTopicTopicIdAndDifficultyLessThanEqual(topicId, maxDifficulty);
        } else {
            candidates = questionRepository.findByTopicTopicIdAndDifficulty(topicId, maxDifficulty);
        }
        
        // Filter out answered questions
        candidates = candidates.stream()
            .filter(q -> !excludeIds.contains(q.getId()))
            .collect(Collectors.toList());
        
        // Sort by difficulty (closest to target)
        candidates.sort((a, b) -> Math.abs(a.getDifficulty() - maxDifficulty) - 
                                   Math.abs(b.getDifficulty() - maxDifficulty));
        
        return candidates.isEmpty() ? null : candidates.get(0);
    }
    
    private Question findQuestionFromTopics(List<String> topicIds, Set<Long> excludeIds) {
        for (String topicId : topicIds) {
            List<Question> candidates = questionRepository.findByTopicTopicId(topicId);
            candidates = candidates.stream()
                .filter(q -> !excludeIds.contains(q.getId()))
                .collect(Collectors.toList());
            
            if (!candidates.isEmpty()) {
                return candidates.get(new Random().nextInt(candidates.size()));
            }
        }
        return null;
    }
    
    private Question getRandomUnansweredWithExclude(Set<Long> excludeIds) {
        List<Question> all = questionRepository.findAll();
        List<Question> unanswered = all.stream()
            .filter(q -> !excludeIds.contains(q.getId()))
            .collect(Collectors.toList());
        
        return unanswered.isEmpty() ? null : unanswered.get(new Random().nextInt(unanswered.size()));
    }
    
    private Question getRandomQuestion(Set<Long> excludeIds) {
        List<Question> all = questionRepository.findAll();
        List<Question> available = all.stream()
            .filter(q -> !excludeIds.contains(q.getId()))
            .collect(Collectors.toList());
        
        return available.isEmpty() ? null : available.get(new Random().nextInt(available.size()));
    }
}
