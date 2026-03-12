package com.javamentor.recommend;

import com.javamentor.question.dto.QuestionDto;
import com.javamentor.question.entity.Question;
import com.javamentor.question.service.QuestionService;
import com.javamentor.progress.repository.UserProgressRepository;
import com.javamentor.session.service.SessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Recommend Service - 智能推薦邏輯 (7-Rule Algorithm)
 */
@Service
public class RecommendService {

    private static final Logger log = LoggerFactory.getLogger(RecommendService.class);

    private final QuestionService questionService;
    private final UserProgressRepository userProgressRepository;
    private final SessionService sessionService;

    public RecommendService(QuestionService questionService,
                        UserProgressRepository userProgressRepository,
                        SessionService sessionService) {
        this.questionService = questionService;
        this.userProgressRepository = userProgressRepository;
        this.sessionService = sessionService;
    }

    /**
     * 推薦下一題
     */
    @Transactional(readOnly = true)
    public QuestionDto recommendNextQuestion(String sessionId, Long currentQuestionId, boolean correct) {
        Question current = questionService.getQuestionEntityById(currentQuestionId);
        
        Set<Long> answeredIds = getAnsweredQuestionIds(sessionId);
        Map<String, Double> topicAccuracy = calculateTopicAccuracy(sessionId);
        List<String> weakTopics = getWeakTopics(topicAccuracy);
        
        long recentCountInTopic = getRecentCountInTopic(sessionId, current.getTopic().getTopicId());
        
        // Rule 1: Wrong → same topic, same/lower difficulty
        if (!correct) {
            Question q = findQuestionSameTopic(current.getTopic().getTopicId(), 
                current.getDifficulty(), answeredIds, true);
            if (q != null) return questionService.toDto(q);
        }
        
        // Rule 4: Too many in same topic → inject different
        if (recentCountInTopic >= 3 && !weakTopics.isEmpty()) {
            Question q = findQuestionFromTopics(weakTopics, answeredIds);
            if (q != null) return questionService.toDto(q);
        }
        
        // Rule 5: Weak topics → inject
        long totalAnswered = userProgressRepository.countBySessionId(sessionId);
        if (totalAnswered % 10 == 0 && !weakTopics.isEmpty()) {
            Question q = findQuestionFromTopics(weakTopics, answeredIds);
            if (q != null) return questionService.toDto(q);
        }
        
        // Rule 2-3: Correct → escalate difficulty
        if (correct) {
            Integer diff = current.getDifficulty();
            if (diff != null && diff == 1) {
                Question q = findQuestionSameTopic(current.getTopic().getTopicId(), 2, answeredIds, false);
                if (q != null) return questionService.toDto(q);
            } else if (diff != null && diff == 2) {
                Question q = findQuestionSameTopic(current.getTopic().getTopicId(), 3, answeredIds, false);
                if (q != null) return questionService.toDto(q);
            }
        }
        
        // Fallback: random question
        return questionService.toDto(getRandomQuestion(answeredIds));
    }

    /**
     * 搵相關題目
     */
    @Transactional(readOnly = true)
    public List<QuestionDto> findRelatedQuestions(Long currentQuestionId, boolean answeredCorrect) {
        Question current = questionService.getQuestionEntityById(currentQuestionId);
        
        if (current.getTags() == null) return Collections.emptyList();

        String topicId = current.getTopic().getTopicId();
        List<Question> candidates = questionService.getQuestionsByTopic(topicId);
        
        Set<String> currentTags = current.getTags();
        int currentDifficulty = current.getDifficulty() != null ? current.getDifficulty() : 1;

        return candidates.stream()
                .filter(q -> !q.getId().equals(currentQuestionId))
                .filter(q -> q.getTags() != null && hasSharedTags(currentTags, q.getTags()))
                .filter(q -> {
                    int d = q.getDifficulty() != null ? q.getDifficulty() : 1;
                    return answeredCorrect ? d >= currentDifficulty : d <= currentDifficulty;
                })
                .sorted(Comparator.comparingInt(q -> q.getDifficulty() != null ? q.getDifficulty() : 1))
                .limit(2)
                .map(questionService::toDto)
                .collect(Collectors.toList());
    }

    private Set<Long> getAnsweredQuestionIds(String sessionId) {
        return userProgressRepository.findBySessionIdOrderByAnsweredAtDesc(sessionId)
                .stream()
                .map(p -> p.getQuestion().getId())
                .collect(Collectors.toSet());
    }

    private Map<String, Double> calculateTopicAccuracy(String sessionId) {
        List<Object[]> stats = userProgressRepository.getTopicStatsGrouped(sessionId);
        Map<String, Double> accuracy = new HashMap<>();
        
        for (Object[] row : stats) {
            String topicId = (String) row[0];
            long total = ((Number) row[1]).longValue();
            long correct = ((Number) row[2]).longValue();
            accuracy.put(topicId, total > 0 ? (double) correct / total : 0);
        }
        
        return accuracy;
    }

    private List<String> getWeakTopics(Map<String, Double> topicAccuracy) {
        return topicAccuracy.entrySet().stream()
                .filter(e -> e.getValue() < 0.6)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private long getRecentCountInTopic(String sessionId, String topicId) {
        return userProgressRepository.findBySessionIdOrderByAnsweredAtDesc(sessionId)
                .stream()
                .filter(p -> p.getQuestion().getTopic().getTopicId().equals(topicId))
                .count();
    }

    private Question findQuestionSameTopic(String topicId, int maxDifficulty, 
                                         Set<Long> excludeIds, boolean allowLower) {
        List<Question> candidates = questionService.getQuestionsByTopic(topicId);
        
        return candidates.stream()
            .filter(q -> !excludeIds.contains(q.getId()))
            .filter(q -> {
                int d = q.getDifficulty() != null ? q.getDifficulty() : 1;
                return allowLower ? d <= maxDifficulty : d == maxDifficulty;
            })
            .findFirst()
            .orElse(null);
    }

    private Question findQuestionFromTopics(List<String> topicIds, Set<Long> excludeIds) {
        for (String topicId : topicIds) {
            List<Question> candidates = questionService.getQuestionsByTopic(topicId);
            Optional<Question> q = candidates.stream()
                .filter(c -> !excludeIds.contains(c.getId()))
                .findAny();
            if (q.isPresent()) return q.get();
        }
        return null;
    }

    private Question getRandomQuestion(Set<Long> excludeIds) {
        List<Question> all = questionService.getAllTopics()
                .stream()
                .flatMap(t -> questionService.getQuestionsByTopic(t.getTopicId()).stream())
                .filter(q -> !excludeIds.contains(q.getId()))
                .toList();
        
        return all.isEmpty() ? null : all.get(new Random().nextInt(all.size()));
    }

    private boolean hasSharedTags(Set<String> tags1, Set<String> tags2) {
        if (tags2 == null || tags2.isEmpty()) return false;
        return !Collections.disjoint(tags1, tags2);
    }
}
