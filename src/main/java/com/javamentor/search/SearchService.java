package com.javamentor.search;

import com.javamentor.question.dto.QuestionDto;
import com.javamentor.question.entity.Question;
import com.javamentor.question.repository.QuestionRepository;
import com.javamentor.question.service.QuestionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Search Service - 負責題目搜尋
 */
@Service
public class SearchService {

    private static final Logger log = LoggerFactory.getLogger(SearchService.class);

    private final QuestionRepository questionRepository;
    private final QuestionService questionService;

    public SearchService(QuestionRepository questionRepository, QuestionService questionService) {
        this.questionRepository = questionRepository;
        this.questionService = questionService;
    }

    private static final int MAX_LOG_LENGTH = 50;
    
    /**
     * 搜尋題目 by keyword with pagination
     */
    @Transactional(readOnly = true)
    public Page<QuestionDto> search(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Page.empty(pageable);
        }
        
        String sanitizedKeyword = keyword.trim();
        log.info("Searching questions with keyword: {}, page: {}, size: {}", 
                truncateForLog(sanitizedKeyword), 
                pageable.getPageNumber(), 
                pageable.getPageSize());
        
        Page<Question> results = questionRepository.searchByKeyword(sanitizedKeyword, pageable);
        
        return results.map(questionService::toDto);
    }

    /**
     * 搜尋題目 by keyword in specific topic with pagination
     */
    @Transactional(readOnly = true)
    public Page<QuestionDto> searchByTopic(String keyword, String topicId, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty() || topicId == null) {
            return Page.empty(pageable);
        }
        
        String sanitizedKeyword = keyword.trim();
        log.info("Searching questions with keyword: {} in topic: {}, page: {}, size: {}", 
                truncateForLog(sanitizedKeyword), 
                topicId, 
                pageable.getPageNumber(), 
                pageable.getPageSize());
        
        Page<Question> results = questionRepository.searchByKeywordAndTopic(sanitizedKeyword, topicId, pageable);
        
        return results.map(questionService::toDto);
    }
    
    /**
     * Truncate keyword for logging to avoid log bloat
     */
    private String truncateForLog(String input) {
        if (input == null) {
            return "";
        }
        return input.length() > MAX_LOG_LENGTH 
            ? input.substring(0, MAX_LOG_LENGTH) + "..." 
            : input;
    }
}
