package com.javamentor.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javamentor.entity.*;
import com.javamentor.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.*;

/**
 * JSON Question Loader - Phase 1
 * 從 questions.json 載入題目
 */
@Component
@Order(1)
public class JsonQuestionLoader implements CommandLineRunner {
    
    private static final Logger log = LoggerFactory.getLogger(JsonQuestionLoader.class);
    
    private final TopicRepository topicRepository;
    private final QuestionRepository questionRepository;
    private final ObjectMapper objectMapper;
    
    public JsonQuestionLoader(TopicRepository topicRepository, QuestionRepository questionRepository) {
        this.topicRepository = topicRepository;
        this.questionRepository = questionRepository;
        this.objectMapper = new ObjectMapper();
    }
    
    @Override
    public void run(String... args) throws Exception {
        // 如果已經有題目，跳過
        if (questionRepository.count() > 0) {
            log.info("Questions already loaded, skipping JSON loader");
            return;
        }
        
        log.info("Loading questions from JSON...");
        
        try {
            ClassPathResource resource = new ClassPathResource("data/questions.json");
            InputStream is = resource.getInputStream();
            
            Map<String, Object> data = objectMapper.readValue(is, new TypeReference<Map<String, Object>>() {});
            List<Map<String, Object>> topicsData = (List<Map<String, Object>>) data.get("topics");
            
            List<Topic> savedTopics = new ArrayList<>();
            Map<String, Topic> topicMap = new HashMap<>();
            
            // Create topics first
            for (Map<String, Object> topicData : topicsData) {
                String topicId = (String) topicData.get("topicId");
                String name = (String) topicData.get("name");
                String description = (String) topicData.get("description");
                int questionCount = ((List<?>) topicData.get("questions")).size();
                
                Topic topic = new Topic();
                topic.setTopicId(topicId);
                topic.setName(name);
                topic.setDescription(description);
                topic.setQuestionCount(questionCount);
                
                savedTopics.add(topic);
                topicMap.put(topicId, topic);
            }
            
            topicRepository.saveAll(savedTopics);
            log.info("Created {} topics", savedTopics.size());
            
            // Load questions
            List<Question> questions = new ArrayList<>();
            int totalQuestions = 0;
            
            for (Map<String, Object> topicData : topicsData) {
                String topicId = (String) topicData.get("topicId");
                Topic topic = topicMap.get(topicId);
                List<Map<String, Object>> questionsData = (List<Map<String, Object>>) topicData.get("questions");
                
                for (Map<String, Object> qData : questionsData) {
                    Question q = new Question();
                    q.setTopic(topic);
                    q.setQuestion((String) qData.get("question"));
                    q.setOptionA((String) qData.get("optionA"));
                    q.setOptionB((String) qData.get("optionB"));
                    q.setOptionC((String) qData.get("optionC"));
                    q.setOptionD((String) qData.get("optionD"));
                    q.setOptionE((String) qData.get("optionE"));
                    q.setCorrectAnswer((String) qData.get("correct"));
                    q.setTags((String) qData.get("tags"));
                    q.setDifficulty((Integer) qData.get("difficulty"));
                    q.setMultiSelect((Boolean) qData.getOrDefault("multiSelect", false));
                    q.setDisplayOrder((Integer) qData.get("id"));
                    q.setExplanation((String) qData.get("explanation"));
                    q.setFollowUpCorrect((String) qData.get("followUpCorrect"));
                    q.setFollowUpWrong((String) qData.get("followUpWrong"));
                    
                    questions.add(q);
                    totalQuestions++;
                }
            }
            
            questionRepository.saveAll(questions);
            log.info("Loaded {} questions from JSON", totalQuestions);
            
        } catch (Exception e) {
            log.error("Failed to load questions from JSON: {}", e.getMessage());
            // Fall through - DataInitializer will handle it
        }
    }
}
