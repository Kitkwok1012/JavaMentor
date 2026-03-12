package com.javamentor.question.service;

import com.javamentor.exception.QuestionNotFoundException;
import com.javamentor.exception.TopicNotFoundException;
import com.javamentor.question.dto.QuestionDto;
import com.javamentor.question.entity.Question;
import com.javamentor.question.entity.Topic;
import com.javamentor.question.repository.QuestionRepository;
import com.javamentor.question.repository.TopicRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Question Service - 淨係負責題目同埋 Topic 既查詢
 */
@Service
public class QuestionService {

    private static final Logger log = LoggerFactory.getLogger(QuestionService.class);

    private final QuestionRepository questionRepository;
    private final TopicRepository topicRepository;

    public QuestionService(QuestionRepository questionRepository, TopicRepository topicRepository) {
        this.questionRepository = questionRepository;
        this.topicRepository = topicRepository;
    }

    // ==================== Topic Methods ====================

    @Cacheable("topics")
    @Transactional(readOnly = true)
    public List<Topic> getAllTopics() {
        log.debug("Loading all topics from database");
        return topicRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Topic getTopicById(String topicId) {
        return topicRepository.findByTopicId(topicId)
                .orElseThrow(() -> new TopicNotFoundException(topicId));
    }

    @Transactional(readOnly = true)
    public List<Question> getQuestionsByTopic(String topicId) {
        return questionRepository.findByTopicTopicIdOrderByDisplayOrder(topicId);
    }

    // ==================== Question Methods ====================

    @Transactional(readOnly = true)
    public QuestionDto getQuestionById(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException(questionId));
        return toDto(question);
    }

    @Transactional(readOnly = true)
    public Question getQuestionEntityById(Long questionId) {
        return questionRepository.findById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException(questionId));
    }

    // ==================== Mapper ====================

    public QuestionDto toDto(Question question) {
        if (question == null) return null;
        
        QuestionDto dto = new QuestionDto();
        dto.setId(question.getId());
        dto.setQuestion(question.getQuestion());
        dto.setMultiSelect(question.getMultiSelect());
        dto.setDifficulty(question.getDifficulty());
        
        // Normalized options
        dto.setOptions(question.getOptions().stream()
                .map(opt -> QuestionDto.OptionDto.builder()
                        .label(opt.getLabel())
                        .content(opt.getContent())
                        .build())
                .toList());
        
        if (question.getTopic() != null) {
            dto.setTopicId(question.getTopic().getTopicId());
            dto.setTopicName(question.getTopic().getName());
        }
        
        return dto;
    }
}
