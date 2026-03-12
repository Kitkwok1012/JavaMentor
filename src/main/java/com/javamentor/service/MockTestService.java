package com.javamentor.service;

import com.javamentor.common.utils.AnswerUtils;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.javamentor.mocktest.dto.MockTestDto;
import com.javamentor.question.dto.QuestionDto;
import com.javamentor.question.entity.Question;
import com.javamentor.question.repository.QuestionRepository;
import com.javamentor.question.service.QuestionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Mock Test Service with Caffeine caching
 * Replaces ConcurrentHashMap to prevent memory leaks
 */
@Service
public class MockTestService {
    
    private final QuestionRepository questionRepository;
    private final QuestionService questionService;
    
    // Wrapper class to hold both MockTestDto and answers together
    private static class MockTestSession {
        final MockTestDto mockTest;
        final List<String> answers;

        MockTestSession(MockTestDto mockTest) {
            this.mockTest = mockTest;
            this.answers = Collections.synchronizedList(new ArrayList<>());
        }
    }
    
    // Caffeine cache with TTL (2 hours) and max size to prevent memory leaks
    private final Cache<String, MockTestSession> mockTestCache = Caffeine.newBuilder()
            .expireAfterAccess(2, TimeUnit.HOURS)
            .maximumSize(10_000)
            .build();
    
    public MockTestService(QuestionRepository questionRepository, QuestionService questionService) {
        this.questionRepository = questionRepository;
        this.questionService = questionService;
    }
    
    /**
     * Start a new mock test session
     * @param topics Selected topics (null = all topics)
     * @param count Number of questions (30/60/90)
     * @return MockTestDto with session info
     */
    public MockTestDto startMockTest(List<String> topics, int count) {
        String sessionId = UUID.randomUUID().toString();
        
        List<Question> allQuestions;
        if (topics == null || topics.isEmpty()) {
            // All topics - use repository query instead of findAll()
            allQuestions = questionRepository.findByTopicTopicIdNotNull();
        } else {
            // Specific topics
            allQuestions = topics.stream()
                .flatMap(topicId -> questionRepository.findByTopicTopicId(topicId).stream())
                .collect(Collectors.toList());
        }
        
        // Shuffle and select - operate directly on Question objects
        Collections.shuffle(allQuestions);
        int actualCount = Math.min(count, allQuestions.size());
        
        // Sort by difficulty and select directly - no re-fetch needed
        List<Question> selectedQuestions = allQuestions.stream()
            .limit(actualCount)
            .sorted(Comparator.comparingInt(q -> q.getDifficulty() != null ? q.getDifficulty() : 1))
            .collect(Collectors.toList());
        
        List<Long> questionIds = selectedQuestions.stream()
            .map(Question::getId)
            .collect(Collectors.toList());
        
        MockTestDto mockTest = new MockTestDto();
        mockTest.setSessionId(sessionId);
        mockTest.setQuestionIds(questionIds);
        mockTest.setTotalQuestions(questionIds.size());
        mockTest.setCurrentIndex(0);
        mockTest.setSelectedTopics(topics);
        mockTest.setStartTime(System.currentTimeMillis());
        
        // Store in cache with wrapper
        mockTestCache.put(sessionId, new MockTestSession(mockTest));
        
        return mockTest;
    }
    
    /**
     * Get current question for the session (returns DTO, not Entity)
     */
    @Transactional(readOnly = true)
    public QuestionDto getCurrentQuestion(String sessionId) {
        MockTestSession session = mockTestCache.getIfPresent(sessionId);
        if (session == null) return null;
        
        MockTestDto mockTest = session.mockTest;
        int index = mockTest.getCurrentIndex();
        if (index >= mockTest.getQuestionIds().size()) return null;
        
        Long questionId = mockTest.getQuestionIds().get(index);
        Question question = questionRepository.findById(questionId).orElse(null);
        
        return questionService.toDto(question);
    }
    
    /**
     * Submit answer for current question
     */
    public boolean submitAnswer(String sessionId, String answer) {
        return submitAnswerWithFeedback(sessionId, answer) != null;
    }
    
    /**
     * Submit answer and return result with correct answer for frontend feedback
     */
    @Transactional
    public Map<String, Object> submitAnswerWithFeedback(String sessionId, String answer) {
        MockTestSession session = mockTestCache.getIfPresent(sessionId);
        if (session == null) return null;
        
        MockTestDto mockTest = session.mockTest;
        
        // Get current question ID from cache
        int index = mockTest.getCurrentIndex();
        if (index >= mockTest.getQuestionIds().size()) return null;
        
        Long questionId = mockTest.getQuestionIds().get(index);
        Question question = questionRepository.findById(questionId).orElse(null);
        
        if (question == null) return null;
        
        String correctAnswer = question.getCorrectAnswer();
        boolean isCorrect = AnswerUtils.isCorrect(answer, correctAnswer, question.getMultiSelect());
        
        // Store answer
        session.answers.add(isCorrect ? "1" : "0");
        
        // Move to next question
        mockTest.setCurrentIndex(mockTest.getCurrentIndex() + 1);
        
        // Return result with correct answer for frontend
        Map<String, Object> result = new HashMap<>();
        result.put("correct", isCorrect);
        result.put("correctAnswer", correctAnswer);
        return result;
    }
    
    /**
     * Get test result
     */
    public Map<String, Object> getTestResult(String sessionId) {
        MockTestSession session = mockTestCache.getIfPresent(sessionId);
        if (session == null) return null;
        
        MockTestDto mockTest = session.mockTest;
        List<String> answers = session.answers;
        
        int total = answers.size();
        long correct = answers.stream().filter("1"::equals).count();
        
        Map<String, Object> result = new HashMap<>();
        result.put("sessionId", sessionId);
        result.put("totalQuestions", total);
        result.put("correctAnswers", correct);
        result.put("score", String.format("%d/%d (%.0f%%)", correct, total, total > 0 ? (double) correct / total * 100 : 0));
        result.put("accuracy", total > 0 ? (double) correct / total * 100 : 0);
        result.put("timeSpent", System.currentTimeMillis() - mockTest.getStartTime());
        
        return result;
    }
    
    /**
     * Get progress (questions answered / total)
     */
    public Map<String, Object> getProgress(String sessionId) {
        MockTestSession session = mockTestCache.getIfPresent(sessionId);
        if (session == null) return null;
        
        MockTestDto mockTest = session.mockTest;
        
        Map<String, Object> progress = new HashMap<>();
        progress.put("current", mockTest.getCurrentIndex() + 1);
        progress.put("total", mockTest.getTotalQuestions());
        progress.put("percentage", (double) mockTest.getCurrentIndex() / mockTest.getTotalQuestions() * 100);
        
        return progress;
    }
}
