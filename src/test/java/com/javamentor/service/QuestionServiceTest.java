package com.javamentor.service;

import com.javamentor.entity.Topic;
import com.javamentor.repository.TopicRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuestionServiceTest {

    @Mock
    private TopicRepository topicRepository;
    
    @InjectMocks
    private QuestionService questionService;
    
    @Test
    void testGetTopicById_found() {
        Topic topic = new Topic();
        topic.setTopicId("oop");
        topic.setName("Object-Oriented Programming");
        
        when(topicRepository.findByTopicId("oop")).thenReturn(Optional.of(topic));
        
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
}
