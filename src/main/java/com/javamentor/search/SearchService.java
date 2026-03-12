package com.javamentor.search;

import com.javamentor.question.dto.QuestionDto;
import com.javamentor.question.entity.Question;
import com.javamentor.question.repository.QuestionRepository;
import com.javamentor.question.service.QuestionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
     * 搜尋題目 by keyword
     */
    @Transactional(readOnly = true)
    public List<QuestionDto> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Collections.emptyList();
        }
        
        log.info("Searching questions with keyword: {}", keyword);
        
        List<Question> results = questionRepository.searchByKeyword(keyword.trim());
        
        return results.stream()
                .map(questionService::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 搜尋題目 by keyword in specific topic
     */
    @Transactional(readOnly = true)
    public List<QuestionDto> searchByTopic(String keyword, String topicId) {
        if (keyword == null || keyword.trim().isEmpty() || topicId == null) {
            return Collections.emptyList();
        }
        
        log.info("Searching questions with keyword: {} in topic: {}", keyword, topicId);
        
        List<Question> results = questionRepository.searchByKeywordAndTopic(keyword.trim(), topicId);
        
        return results.stream()
                .map(questionService::toDto)
                .collect(Collectors.toList());
    }
}
