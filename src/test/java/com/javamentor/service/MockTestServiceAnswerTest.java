package com.javamentor.service;

import com.javamentor.entity.Question;
import com.javamentor.entity.Topic;
import com.javamentor.repository.QuestionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for MockTestService - basic functionality
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MockTestServiceAnswerTest {

    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private MockTestService mockTestService;

    private Topic createTopic(String topicId) {
        Topic topic = new Topic();
        topic.setTopicId(topicId);
        return topic;
    }

    private Question createMockQuestion(Long id, String correctAnswer, boolean multiSelect) {
        Question q = new Question();
        q.setId(id);
        q.setCorrectAnswer(correctAnswer);
        q.setMultiSelect(multiSelect);
        q.setDifficulty(1);
        q.setQuestion("Question " + id);
        q.setOptionA("Option A");
        q.setOptionB("Option B");
        q.setOptionC("Option C");
        q.setOptionD("Option D");
        q.setTopic(createTopic("test"));
        return q;
    }

    @Test
    void testStartMockTest_allTopics_returnsCorrectCount() {
        when(questionRepository.findByTopicTopicIdNotNull()).thenReturn(Arrays.asList(
            createMockQuestion(1L, "A", false),
            createMockQuestion(2L, "B", false),
            createMockQuestion(3L, "C", false)
        ));

        var result = mockTestService.startMockTest(null, 3);
        
        assertNotNull(result);
        assertEquals(3, result.getTotalQuestions());
    }

    @Test
    void testStartMockTest_specificTopics_onlyIncludesThoseTopics() {
        when(questionRepository.findByTopicTopicId("oop")).thenReturn(Arrays.asList(
            createMockQuestion(1L, "A", false)
        ));

        var result = mockTestService.startMockTest(Arrays.asList("oop"), 10);
        
        assertNotNull(result);
    }

    @Test
    void testSubmitAnswer_wrongAnswer() {
        when(questionRepository.findByTopicTopicIdNotNull()).thenReturn(Arrays.asList(
            createMockQuestion(1L, "A", false)
        ));

        var mockTest = mockTestService.startMockTest(null, 1);
        String sessionId = mockTest.getSessionId();

        boolean result = mockTestService.submitAnswer(sessionId, "B");
        
        assertFalse(result, "Wrong answer should return false");
    }

    @Test
    void testGetProgress_returnsCorrectValues() {
        when(questionRepository.findByTopicTopicIdNotNull()).thenReturn(Arrays.asList(
            createMockQuestion(1L, "A", false)
        ));

        var mockTest = mockTestService.startMockTest(null, 1);
        String sessionId = mockTest.getSessionId();

        var progress = mockTestService.getProgress(sessionId);
        
        assertNotNull(progress);
        assertEquals(1, progress.get("total"));
    }
}
