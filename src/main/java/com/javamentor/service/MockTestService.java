package com.javamentor.service;

import com.javamentor.dto.MockTestDto;
import com.javamentor.entity.Question;
import com.javamentor.repository.QuestionRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class MockTestService {
    
    private final QuestionRepository questionRepository;
    
    // In-memory mock test sessions
    private final Map<String, MockTestDto> mockTestSessions = new ConcurrentHashMap<>();
    private final Map<String, List<String>> mockTestAnswers = new ConcurrentHashMap<>(); // sessionId -> list of answers
    
    public MockTestService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
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
            // All topics
            allQuestions = questionRepository.findAll();
        } else {
            // Specific topics
            allQuestions = topics.stream()
                .flatMap(topicId -> questionRepository.findByTopicTopicId(topicId).stream())
                .collect(Collectors.toList());
        }
        
        // Shuffle and select
        Collections.shuffle(allQuestions);
        int actualCount = Math.min(count, allQuestions.size());
        List<Long> questionIds = allQuestions.stream()
            .limit(actualCount)
            .map(Question::getId)
            .collect(Collectors.toList());
        
        // Distribution: 30% easy, 50% medium, 20% hard
        List<Question> selectedQuestions = questionIds.stream()
            .map(id -> questionRepository.findById(id).orElse(null))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        
        // Sort by difficulty for balanced distribution
        selectedQuestions.sort(Comparator.comparingInt(q -> q.getDifficulty() != null ? q.getDifficulty() : 1));
        
        questionIds = selectedQuestions.stream()
            .map(Question::getId)
            .collect(Collectors.toList());
        
        MockTestDto mockTest = new MockTestDto();
        mockTest.setSessionId(sessionId);
        mockTest.setQuestionIds(questionIds);
        mockTest.setTotalQuestions(questionIds.size());
        mockTest.setCurrentIndex(0);
        mockTest.setSelectedTopics(topics);
        mockTest.setStartTime(System.currentTimeMillis());
        
        mockTestSessions.put(sessionId, mockTest);
        mockTestAnswers.put(sessionId, new ArrayList<>());
        
        return mockTest;
    }
    
    /**
     * Get current question for the session
     */
    public Question getCurrentQuestion(String sessionId) {
        MockTestDto mockTest = mockTestSessions.get(sessionId);
        if (mockTest == null) return null;
        
        int index = mockTest.getCurrentIndex();
        if (index >= mockTest.getQuestionIds().size()) return null;
        
        Long questionId = mockTest.getQuestionIds().get(index);
        return questionRepository.findById(questionId).orElse(null);
    }
    
    /**
     * Submit answer for current question
     */
    public boolean submitAnswer(String sessionId, String answer) {
        MockTestDto mockTest = mockTestSessions.get(sessionId);
        if (mockTest == null) return false;
        
        Question question = getCurrentQuestion(sessionId);
        if (question == null) return false;
        
        String correctAnswer = question.getCorrectAnswer();
        boolean isCorrect;
        
        if (Boolean.TRUE.equals(question.getMultiSelect())) {
            isCorrect = normalizeAnswer(answer).equals(normalizeAnswer(correctAnswer));
        } else {
            isCorrect = answer.equalsIgnoreCase(correctAnswer);
        }
        
        // Store answer
        List<String> answers = mockTestAnswers.get(sessionId);
        answers.add(isCorrect ? "1" : "0");
        
        // Move to next question
        mockTest.setCurrentIndex(mockTest.getCurrentIndex() + 1);
        
        return isCorrect;
    }
    
    /**
     * Get test result
     */
    public Map<String, Object> getTestResult(String sessionId) {
        MockTestDto mockTest = mockTestSessions.get(sessionId);
        if (mockTest == null) return null;
        
        List<String> answers = mockTestAnswers.get(sessionId);
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
        MockTestDto mockTest = mockTestSessions.get(sessionId);
        if (mockTest == null) return null;
        
        Map<String, Object> progress = new HashMap<>();
        progress.put("current", mockTest.getCurrentIndex() + 1);
        progress.put("total", mockTest.getTotalQuestions());
        progress.put("percentage", (double) mockTest.getCurrentIndex() / mockTest.getTotalQuestions() * 100);
        
        return progress;
    }
    
    private String normalizeAnswer(String answer) {
        if (answer == null) return "";
        String cleaned = answer.toUpperCase().replaceAll("\\s+", "");
        if (cleaned.contains(",")) {
            String[] parts = cleaned.split(",");
            Arrays.sort(parts);
            return String.join(",", parts);
        }
        return cleaned;
    }
}
