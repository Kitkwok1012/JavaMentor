package com.javamentor.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javamentor.entity.*;
import com.javamentor.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Component
public class QuestionDataLoader implements CommandLineRunner {
    
    private final TopicRepository topicRepository;
    private final QuestionRepository questionRepository;
    private final ObjectMapper objectMapper;
    
    public QuestionDataLoader(TopicRepository topicRepository, QuestionRepository questionRepository) {
        this.topicRepository = topicRepository;
        this.questionRepository = questionRepository;
        this.objectMapper = new ObjectMapper();
    }
    
    @Override
    public void run(String... args) throws IOException {
        if (topicRepository.count() > 0) {
            return;
        }
        
        // Load questions from JSON
        ClassPathResource resource = new ClassPathResource("questions.json");
        Map<String, Object> data = objectMapper.readValue(resource.getInputStream(), Map.class);
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> topicsData = (List<Map<String, Object>>) data.get("topics");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> questionsData = (List<Map<String, Object>>) data.get("questions");
        
        // Create topics
        Map<String, Topic> topicMap = new HashMap<>();
        for (Map<String, Object> t : topicsData) {
            Topic topic = new Topic();
            String topicId = (String) t.get("id");
            topic.setTopicId(topicId);
            topic.setName((String) t.get("name"));
            topic.setDescription((String) t.get("name"));
            topic.setQuestionCount((Integer) t.get("count"));
            topicMap.put(topicId, topic);
        }
        topicRepository.saveAll(topicMap.values());
        
        // Create questions
        List<Question> questions = new ArrayList<>();
        int order = 1;
        for (Map<String, Object> q : questionsData) {
            String topicId = (String) q.get("topic");
            Topic topic = topicMap.get(topicId);
            if (topic == null) continue;
            
            Question question = new Question();
            question.setTopic(topic);
            question.setQuestion((String) q.get("question"));
            question.setOptionA((String) q.get("A"));
            question.setOptionB((String) q.get("B"));
            question.setOptionC((String) q.get("C"));
            question.setOptionD((String) q.get("D"));
            question.setOptionE((String) q.get("E"));
            question.setCorrectAnswer((String) q.get("answer"));
            question.setExplanation((String) q.get("explanation"));
            question.setTags((String) q.get("tags"));
            question.setDifficulty((Integer) q.get("difficulty"));
            question.setMultiSelect(false);
            question.setDisplayOrder(order++);
            
            questions.add(question);
        }
        
        questionRepository.saveAll(questions);
        System.out.println("Loaded " + questions.size() + " questions from JSON!");
    }
}
