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

    /**
     * 搜尋題目 by keyword with pagination
     */
    @Transactional(readOnly = true)
    public Page<QuestionDto> search(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Page.empty(pageable);
        }
        
        log.info("Searching questions with keyword: {}, page: {}, size: {}", keyword, pageable.getPageNumber(), pageable.getPageSize());
        
        Page<Question> results = questionRepository.searchByKeyword(keyword.trim(), pageable);
        
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
        
        log.info("Searching questions with keyword: {} in topic: {}, page: {}, size: {}", keyword, topicId, pageable.getPageNumber(), pageable.getPageSize());
        
        Page<Question> results = questionRepository.searchByKeywordAndTopic(keyword.trim(), topicId, pageable);
        
        return results.map(questionService::toDto);
    }
}
