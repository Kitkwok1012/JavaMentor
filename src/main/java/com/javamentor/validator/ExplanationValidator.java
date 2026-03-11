package com.javamentor.validator;

import com.javamentor.entity.Question;
import com.javamentor.repository.QuestionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ExplanationValidator implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(ExplanationValidator.class);
    private static final int MIN_EXPLANATION_LENGTH = 50;
    
    private final QuestionRepository questionRepository;
    
    public ExplanationValidator(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }
    
    @Override
    public void run(String... args) {
        logger.info("=== Starting Explanation Quality Validation ===");
        
        List<Question> allQuestions = questionRepository.findAll();
        
        int totalQuestions = allQuestions.size();
        int shortExplanations = 0;
        int goodExplanations = 0;
        
        for (Question question : allQuestions) {
            String explanation = question.getExplanation();
            
            if (explanation == null || explanation.length() < MIN_EXPLANATION_LENGTH) {
                shortExplanations++;
                logger.warn("⚠️ Question ID {} has short explanation ({} chars): {}", 
                    question.getId(), 
                    explanation != null ? explanation.length() : 0,
                    truncate(question.getQuestion(), 50));
            } else {
                goodExplanations++;
            }
        }
        
        logger.info("=== Validation Summary ===");
        logger.info("Total Questions: {}", totalQuestions);
        logger.info("Good Explanations (50+ chars): {}", goodExplanations);
        logger.info("Short Explanations (<50 chars): {}", shortExplanations);
        
        if (shortExplanations > 0) {
            logger.warn("⚠️ WARNING: {} questions have explanations shorter than {} characters!", 
                shortExplanations, MIN_EXPLANATION_LENGTH);
        } else {
            logger.info("✅ All explanations meet the minimum length requirement ({} chars)", MIN_EXPLANATION_LENGTH);
        }
        
        logger.info("=== Validation Complete ===");
    }
    
    private String truncate(String str, int maxLength) {
        if (str == null) return "null";
        return str.length() <= maxLength ? str : str.substring(0, maxLength) + "...";
    }
}
