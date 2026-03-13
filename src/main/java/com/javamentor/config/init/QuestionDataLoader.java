package com.javamentor.config.init;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javamentor.question.entity.Question;
import com.javamentor.question.entity.Topic;
import com.javamentor.question.repository.QuestionRepository;
import com.javamentor.question.repository.TopicRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Component
public class QuestionDataLoader implements CommandLineRunner {
    
    private static final Logger log = LoggerFactory.getLogger(QuestionDataLoader.class);
    
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
            log.info("Topics already loaded, skipping...");
            return;
        }
        
        // Load questions from both JSON files
        List<Map<String, Object>> allTopicsData = new ArrayList<>();
        
        // Load main questions
        allTopicsData.addAll(loadTopicsFromJson("data/questions.json"));
        
        // Load senior questions
        allTopicsData.addAll(loadTopicsFromJson("data/questions-senior.json"));
        
        // Load advanced questions
        allTopicsData.addAll(loadTopicsFromJson("data/questions-advanced.json"));
        
        // Load interview scenario deep questions
        allTopicsData.addAll(loadTopicsFromJson("data/questions-interview-scenario.json"));
        
        // Load big tech interview questions
        allTopicsData.addAll(loadTopicsFromJson("data/questions-big-tech.json"));
        
        // Load learning roadmap questions
        allTopicsData.addAll(loadTopicsFromJson("data/questions-learning-roadmap.json"));
        
        // Create topics
        Map<String, Topic> topicMap = new HashMap<>();
        for (Map<String, Object> t : allTopicsData) {
            Topic topic = new Topic();
            String topicId = (String) t.get("topicId");
            topic.setTopicId(topicId);
            topic.setName((String) t.get("name"));
            topic.setDescription((String) t.get("description"));
            
            topicMap.put(topicId, topic);
        }
        topicRepository.saveAll(topicMap.values());
        log.info("Loaded {} topics!", topicMap.size());
        
        // Create questions
        List<Question> questions = new ArrayList<>();
        int order = 1;
        
        for (Map<String, Object> t : allTopicsData) {
            String topicId = (String) t.get("topicId");
            Topic topic = topicMap.get(topicId);
            
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> questionsList = (List<Map<String, Object>>) t.get("questions");
            if (questionsList == null) continue;
            
            for (Map<String, Object> q : questionsList) {
                Question question = new Question();
                question.setTopic(topic);
                question.setQuestion((String) q.get("question"));
                question.setExplanation((String) q.get("explanation"));
                
                // Handle tags
                Object tagsObj = q.get("tags");
                if (tagsObj instanceof String tagsStr && !tagsStr.isEmpty()) {
                    String[] tagArray = tagsStr.split(",");
                    for (String tag : tagArray) {
                        question.addTag(tag.trim());
                    }
                }
                
                // Handle difficulty
                Object diff = q.get("difficulty");
                if (diff instanceof Integer) {
                    question.setDifficulty((Integer) diff);
                } else if (diff instanceof String) {
                    question.setDifficulty(Integer.parseInt((String) diff));
                } else {
                    question.setDifficulty(2);
                }
                
                question.setDisplayOrder(order++);
                
                // Handle multi-select - automatically based on correct answer count
                String correctAnswer = (String) q.get("correct");
                // Just store the correct answer - getMultiSelect() is computed automatically
                
                // Create options
                String[] labels = {"A", "B", "C", "D", "E"};
                for (String label : labels) {
                    String optionKey = "option" + label;
                    String content = (String) q.get(optionKey);
                    if (content != null && !content.isEmpty()) {
                        boolean isCorrect = correctAnswer != null && correctAnswer.contains(label);
                        question.addOption(label, content, isCorrect);
                    }
                }
                
                questions.add(question);
            }
        }
        
        questionRepository.saveAll(questions);
        log.info("Loaded {} questions from JSON!", questions.size());
    }
    
    private List<Map<String, Object>> loadTopicsFromJson(String path) {
        try {
            ClassPathResource resource = new ClassPathResource(path);
            if (!resource.exists()) {
                log.warn("Resource {} not found, skipping...", path);
                return Collections.emptyList();
            }
            Map<String, Object> data = objectMapper.readValue(resource.getInputStream(), Map.class);
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> topicsData = (List<Map<String, Object>>) data.get("topics");
            log.info("Loaded {} topics from {}", topicsData != null ? topicsData.size() : 0, path);
            return topicsData != null ? topicsData : Collections.emptyList();
        } catch (Exception e) {
            log.error("Failed to load {}: {}", path, e.getMessage());
            return Collections.emptyList();
        }
    }
}
