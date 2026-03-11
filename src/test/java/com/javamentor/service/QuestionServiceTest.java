package com.javamentor.service;

import com.javamentor.entity.Topic;
import com.javamentor.repository.QuestionRepository;
import com.javamentor.repository.TopicRepository;
import com.javamentor.repository.UserProgressRepository;
import com.javamentor.repository.UserSessionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for QuestionService - basic topic operations
 */
@ExtendWith(MockitoExtension.class)
class QuestionServiceTest {

    @Mock
    private TopicRepository topicRepository;
    
    @Mock
    private QuestionRepository questionRepository;
    
    @Mock
    private UserProgressRepository userProgressRepository;
    
    @Mock
    private UserSessionRepository userSessionRepository;
    
    // Use manual instantiation to avoid Mockito issues
    private QuestionService questionService;
    
    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        // Create real instances for dependencies
        FollowUpRecommender followUpRecommender = new FollowUpRecommender(
            questionRepository, 
            userProgressRepository
        );
        
        // Use real ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();
        
        questionService = new QuestionService(
            questionRepository,
            topicRepository,
            userProgressRepository,
            userSessionRepository,
            followUpRecommender,
            objectMapper
        );
    }
    
    // ========== Topic Tests ==========
    
    @Test
    void testGetTopicById_found() {
        Topic testTopic = new Topic();
        testTopic.setTopicId("oop");
        testTopic.setName("Object-Oriented Programming");
        
        when(topicRepository.findByTopicId("oop")).thenReturn(Optional.of(testTopic));
        
        Topic result = questionService.getTopicById("oop");
        
        assertNotNull(result);
        assertEquals("oop", result.getTopicId());
    }
    
    @Test
    void testGetTopicById_notFound() {
        when(topicRepository.findByTopicId("nonexistent")).thenReturn(Optional.empty());
        
        assertThrows(Exception.class, () -> {
            questionService.getTopicById("nonexistent");
        });
    }
    
    @Test
    void testGetAllTopics() {
        Topic topic1 = new Topic();
        topic1.setTopicId("oop");
        
        Topic topic2 = new Topic();
        topic2.setTopicId("collection");
        
        when(topicRepository.findAll()).thenReturn(Arrays.asList(topic1, topic2));
        
        List<Topic> result = questionService.getAllTopics();
        
        assertNotNull(result);
        assertEquals(2, result.size());
    }
    
    @Test
    void testGetAllTopics_empty() {
        when(topicRepository.findAll()).thenReturn(new ArrayList<>());
        
        List<Topic> result = questionService.getAllTopics();
        
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
