package com.javamentor.service;

import com.javamentor.exception.InvalidAnswerException;
import com.javamentor.mocktest.dto.AnswerResponseDto;
import com.javamentor.progress.service.ProgressService;
import com.javamentor.question.service.QuestionService;
import com.javamentor.session.service.SessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.javamentor.common.utils.AnswerUtils;
import java.util.Arrays;

/**
 * Answer Service - 負責答題邏輯
 */
@Service
public class AnswerService {

    private static final Logger log = LoggerFactory.getLogger(AnswerService.class);

    private final QuestionService questionService;
    private final SessionService sessionService;
    private final ProgressService progressService;

    public AnswerService(QuestionService questionService,
                       SessionService sessionService,
                       ProgressService progressService) {
        this.questionService = questionService;
        this.sessionService = sessionService;
        this.progressService = progressService;
    }

    /**
     * 提交答案
     */
    @Transactional
    public AnswerResponseDto submitAnswer(String sessionId, Long questionId, String answer) {
        // Validate input
        if (questionId == null) {
            throw new InvalidAnswerException("Question ID 不能為空");
        }
        if (answer == null || answer.trim().isEmpty()) {
            throw new InvalidAnswerException("答案不能為空");
        }
        answer = answer.trim().toUpperCase();

        // Get question
        var question = questionService.getQuestionEntityById(questionId);

        // Check answer
        boolean isCorrect = AnswerUtils.isCorrect(answer, question.getCorrectAnswer(), question.getMultiSelect());

        // Record progress
        progressService.recordAnswer(sessionId, questionId, answer, isCorrect);

        log.info("Answer submitted for session {}, question {}: correct={}", sessionId, questionId, isCorrect);

        // Build response
        AnswerResponseDto response = new AnswerResponseDto();
        response.setCorrect(isCorrect);
        response.setCorrectAnswer(question.getCorrectAnswer());
        response.setExplanation(question.getExplanation());

        // Follow-up question
        String followUp = isCorrect ? question.getFollowUpCorrect() : question.getFollowUpWrong();
        if (followUp != null && !followUp.isBlank()) {
            response.setHasFollowUp(true);
            response.setFollowUpQuestion(followUp);
            // Use default A/B options for follow-up when dynamic options not available
            response.setFollowUpOptions(Arrays.asList("A", "B"));
        } else {
            response.setHasFollowUp(false);
        }

        // Check if last question
        String topicId = question.getTopic().getTopicId();
        response.setIsLastQuestion(sessionService.isLastQuestion(sessionId, topicId));

        return response;
    }
}
