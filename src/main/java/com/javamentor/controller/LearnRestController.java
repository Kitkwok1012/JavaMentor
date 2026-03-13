package com.javamentor.controller;

import com.javamentor.filter.SessionFilter;
import com.javamentor.question.dto.QuestionDto;
import com.javamentor.recommend.RecommendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Learn REST API Controller
 * Handles REST endpoints for learning features
 * 
 * All endpoints are under /api/v1/learn/**
 */
@RestController
@RequestMapping("/api/v1/learn")
@Tag(name = "Learn API", description = "學習相關 REST API (v1)")
@Validated
public class LearnRestController {

    private final RecommendService recommendService;

    public LearnRestController(RecommendService recommendService) {
        this.recommendService = recommendService;
    }

    private String getSessionId(HttpServletRequest request) {
        return (String) request.getAttribute(SessionFilter.SESSION_ATTRIBUTE);
    }

    @Operation(summary = "相關題目", description = "獲取與當前題目相關既其他題目")
    @GetMapping("/related")
    public List<QuestionDto> getRelatedQuestions(
            @Parameter(description = "Question ID") 
            @RequestParam @NotNull Long questionId,
            @Parameter(description = "是否答啱") 
            @RequestParam boolean correct) {
        return recommendService.findRelatedQuestions(questionId, correct);
    }

    @Operation(summary = "智能推薦", description = "根據當前答題情況推薦下一題")
    @GetMapping("/recommend")
    public QuestionDto recommendNext(
            @Parameter(description = "Question ID") 
            @RequestParam @NotNull Long questionId,
            @Parameter(description = "是否答啱") 
            @RequestParam boolean correct,
            HttpServletRequest request) {
        String sessionId = getSessionId(request);
        return recommendService.recommendNextQuestion(sessionId, questionId, correct);
    }
}
