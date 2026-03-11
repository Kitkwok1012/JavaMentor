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
            System.out.println("Topics already loaded, skipping...");
            return;
        }
        
        // Load questions from JSON (new format: data/questions.json)
        ClassPathResource resource = new ClassPathResource("data/questions.json");
        Map<String, Object> data = objectMapper.readValue(resource.getInputStream(), Map.class);
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> topicsData = (List<Map<String, Object>>) data.get("topics");
        
        // Create topics
        Map<String, Topic> topicMap = new HashMap<>();
        for (Map<String, Object> t : topicsData) {
            Topic topic = new Topic();
            String topicId = (String) t.get("topicId");
            topic.setTopicId(topicId);
            topic.setName((String) t.get("name"));
            topic.setDescription((String) t.get("description"));
            
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> questionsList = (List<Map<String, Object>>) t.get("questions");
            topic.setQuestionCount(questionsList != null ? questionsList.size() : 0);
            
            topicMap.put(topicId, topic);
        }
        topicRepository.saveAll(topicMap.values());
        System.out.println("Loaded " + topicMap.size() + " topics!");
        
        // Create questions
        List<Question> questions = new ArrayList<>();
        int order = 1;
        
        for (Map<String, Object> t : topicsData) {
            String topicId = (String) t.get("topicId");
            Topic topic = topicMap.get(topicId);
            
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> questionsList = (List<Map<String, Object>>) t.get("questions");
            if (questionsList == null) continue;
            
            for (Map<String, Object> q : questionsList) {
                Question question = new Question();
                question.setTopic(topic);
                question.setQuestion((String) q.get("question"));
                question.setOptionA((String) q.get("optionA"));
                question.setOptionB((String) q.get("optionB"));
                question.setOptionC((String) q.get("optionC"));
                question.setOptionD((String) q.get("optionD"));
                question.setOptionE((String) q.get("optionE"));
                question.setCorrectAnswer((String) q.get("correct"));
                question.setExplanation((String) q.get("explanation"));
                question.setTags((String) q.get("tags"));
                
                // Handle difficulty
                Object diff = q.get("difficulty");
                if (diff instanceof Integer) {
                    question.setDifficulty((Integer) diff);
                } else if (diff instanceof String) {
                    question.setDifficulty(Integer.parseInt((String) diff));
                } else {
                    question.setDifficulty(2);
                }
                
                // Handle multiSelect
                Object multi = q.get("multiSelect");
                if (multi instanceof Boolean) {
                    question.setMultiSelect((Boolean) multi);
                } else if (multi instanceof String) {
                    question.setMultiSelect(Boolean.parseBoolean((String) multi));
                } else {
                    question.setMultiSelect(false);
                }
                
                question.setDisplayOrder(order++);
                questions.add(question);
            }
        }
        
        questionRepository.saveAll(questions);
        System.out.println("Loaded " + questions.size() + " questions from JSON!");
    }
}
