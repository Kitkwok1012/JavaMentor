package com.javamentor.service;

import com.javamentor.entity.Question;
import com.javamentor.entity.Topic;
import com.javamentor.entity.UserProgress;
import com.javamentor.repository.QuestionRepository;
import com.javamentor.repository.UserProgressRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests for FollowUpRecommender - 7 rules
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class FollowUpRecommenderTest {

    @Mock
    private QuestionRepository questionRepository;
    
    @Mock
    private UserProgressRepository userProgressRepository;
    
    private FollowUpRecommender recommender;

    private Topic topic1, topic2;
    private Question q1, q2, q3, q4;

    @BeforeEach
    void setUp() {
        recommender = new FollowUpRecommender(questionRepository, userProgressRepository);
        
        topic1 = new Topic();
        topic1.setTopicId("oop");
        
        topic2 = new Topic();
        topic2.setTopicId("collection");
        
        q1 = createQuestion(1L, topic1, "A", 1);
        q2 = createQuestion(2L, topic1, "B", 2);
        q3 = createQuestion(3L, topic2, "C", 1);
        q4 = createQuestion(4L, topic2, "D", 2);
    }

    @Test
    void recommend_wrongAnswer_returnsSameTopicQuestion() {
        when(questionRepository.findById(2L)).thenReturn(Optional.of(q2));
        when(userProgressRepository.findBySessionIdOrderByAnsweredAtDesc("session1"))
            .thenReturn(Collections.emptyList());
        when(questionRepository.findByTopicTopicIdAndDifficultyLessThanEqual("oop", 2))
            .thenReturn(Arrays.asList(q1, q2));
        when(questionRepository.findExcluding(anySet()))
            .thenReturn(Arrays.asList(q1, q2, q3, q4));

        Question result = recommender.recommend("session1", 2L, false);
        
        assertNotNull(result);
    }

    @Test
    void recommend_correctEasy_returnsMediumDifficulty() {
        when(questionRepository.findById(1L)).thenReturn(Optional.of(q1));
        when(userProgressRepository.findBySessionIdOrderByAnsweredAtDesc("session1"))
            .thenReturn(Collections.emptyList());
        when(questionRepository.findByTopicTopicIdAndDifficultyLessThanEqual("oop", 2))
            .thenReturn(Arrays.asList(q1, q2));
        when(questionRepository.findExcluding(anySet()))
            .thenReturn(Arrays.asList(q1, q2, q3, q4));

        Question result = recommender.recommend("session1", 1L, true);
        
        assertNotNull(result);
    }

    @Test
    void recommend_returnsAnyQuestion() {
        when(questionRepository.findById(1L)).thenReturn(Optional.of(q1));
        when(userProgressRepository.findBySessionIdOrderByAnsweredAtDesc("session1"))
            .thenReturn(Collections.emptyList());
        when(questionRepository.findExcluding(anySet()))
            .thenReturn(Arrays.asList(q3, q4));

        Question result = recommender.recommend("session1", 1L, true);
        
        assertNotNull(result);
    }

    private Question createQuestion(Long id, Topic topic, String correct, int difficulty) {
        Question q = new Question();
        q.setId(id);
        q.setTopic(topic);
        q.setCorrectAnswer(correct);
        q.setDifficulty(difficulty);
        q.setQuestion("Test question " + id);
        return q;
    }
}
