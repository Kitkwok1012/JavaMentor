package com.javamentor.controller;

import com.javamentor.filter.SessionFilter;
import com.javamentor.mocktest.dto.AnswerResponseDto;
import com.javamentor.progress.dto.TopicProgressDto;
import com.javamentor.progress.service.ProgressService;
import com.javamentor.question.dto.QuestionDto;
import com.javamentor.question.service.QuestionService;
import com.javamentor.recommend.RecommendService;
import com.javamentor.search.SearchService;
import com.javamentor.session.service.SessionService;
import com.javamentor.service.AnswerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@Validated
@Tag(name = "Learn Controller", description = "題目學習與答題 API")
public class LearnController {

    private static final Logger log = LoggerFactory.getLogger(LearnController.class);

    private final QuestionService questionService;
    private final SessionService sessionService;
    private final ProgressService progressService;
    private final AnswerService answerService;
    private final RecommendService recommendService;
    private final SearchService searchService;

    public LearnController(QuestionService questionService,
                        SessionService sessionService,
                        ProgressService progressService,
                        AnswerService answerService,
                        RecommendService recommendService,
                        SearchService searchService) {
        this.questionService = questionService;
        this.sessionService = sessionService;
        this.progressService = progressService;
        this.answerService = answerService;
        this.recommendService = recommendService;
        this.searchService = searchService;
    }

    private String getSessionId(HttpServletRequest request) {
        return (String) request.getAttribute(SessionFilter.SESSION_ATTRIBUTE);
    }

    @Operation(summary = "首頁", description = "獲取首頁數據，包含學習進度和統計")
    @GetMapping("/")
    public String index(Model model, HttpServletRequest request) {
        String sessionId = getSessionId(request);
        List<TopicProgressDto> progress = progressService.getTopicProgress(sessionId);
        Map<String, Object> stats = progressService.getUserStats(sessionId);
        
        model.addAttribute("progress", progress);
        model.addAttribute("stats", stats);
        return "index";
    }

    @Operation(summary = "開始學習 Topic", description = "根據 Topic ID 獲取題目進行學習")
    @GetMapping("/learn/{topicId}")
    public String learnTopic(
            @Parameter(description = "Topic ID") @PathVariable String topicId, 
            Model model, HttpServletRequest request) {
        String sessionId = getSessionId(request);
        
        var topic = questionService.getTopicById(topicId);
        
        // Get next question ID from session
        var questionIdOpt = sessionService.getNextQuestionId(sessionId, topicId);
        
        QuestionDto question = null;
        if (questionIdOpt.isPresent()) {
            question = questionService.getQuestionById(questionIdOpt.get());
        }

        model.addAttribute("topic", topic);
        model.addAttribute("question", question);

        if (question == null) {
            model.addAttribute("completed", true);
        }

        return "learn";
    }

    @Operation(summary = "下一題", description = "移動到下一題")
    @GetMapping("/question/next")
    public String getNextQuestion(
            @Parameter(description = "Topic ID") @RequestParam @NotBlank String topicId, 
            Model model, HttpServletRequest request) {
        String sessionId = getSessionId(request);
        
        sessionService.moveToNextQuestion(sessionId, topicId);

        var questionIdOpt = sessionService.getNextQuestionId(sessionId, topicId);
        
        QuestionDto question = null;
        if (questionIdOpt.isPresent()) {
            question = questionService.getQuestionById(questionIdOpt.get());
        }
        
        var topic = questionService.getTopicById(topicId);

        model.addAttribute("topic", topic);
        model.addAttribute("question", question);

        if (question == null) {
            model.addAttribute("completed", true);
        }

        return "learn";
    }

    @Operation(summary = "提交答案", description = "提交問題答案並獲取結果")
    @PostMapping("/answer")
    public String submitAnswer(
            @Parameter(description = "Question ID") @RequestParam @NotNull Long questionId,
            @Parameter(description = "答案 (A/B/C/D)") @RequestParam @NotBlank String answer,
            @Parameter(description = "Topic ID") @RequestParam @NotBlank String topicId,
            Model model,
            HttpServletRequest request) {

        String sessionId = getSessionId(request);
        
        log.info("Submitting answer: sessionId={}, questionId={}, answer={}, topicId={}", 
                sessionId, questionId, answer, topicId);

        try {
            var question = questionService.getQuestionById(questionId);
            AnswerResponseDto response = answerService.submitAnswer(sessionId, questionId, answer);
            var topic = questionService.getTopicById(topicId);

            model.addAttribute("topic", topic);
            model.addAttribute("question", question);
            model.addAttribute("response", response);
            model.addAttribute("questionId", questionId);
            model.addAttribute("topicId", topicId);

            return "learn";

        } catch (Exception e) {
            log.error("Error submitting answer", e);
            model.addAttribute("error", e.getMessage());
            return "redirect:/learn/" + topicId;
        }
    }

    @Operation(summary = "錯題本", description = "獲取用戶既錯題列表")
    @GetMapping("/wrong")
    public String wrongQuestions(Model model, HttpServletRequest request) {
        String sessionId = getSessionId(request);
        var wrongQuestions = progressService.getWrongQuestions(sessionId);
        model.addAttribute("wrongQuestions", wrongQuestions);
        return "wrong";
    }

    @Operation(summary = "學習進度", description = "查看 overall learning progress")
    @GetMapping("/progress")
    public String progress(Model model, HttpServletRequest request) {
        String sessionId = getSessionId(request);
        List<TopicProgressDto> progress = progressService.getTopicProgress(sessionId);
        Map<String, Object> stats = progressService.getUserStats(sessionId);
        
        model.addAttribute("progress", progress);
        model.addAttribute("stats", stats);
        return "progress";
    }

    @Operation(summary = "重置 Topic 進度", description = "清除特定 Topic 既學習進度")
    @PostMapping("/learn/{topicId}/reset")
    public String resetTopic(
            @Parameter(description = "Topic ID") @PathVariable String topicId, 
            HttpServletRequest request) {
        String sessionId = getSessionId(request);
        sessionService.resetSession(sessionId, topicId);
        return "redirect:/learn/" + topicId;
    }

    @Operation(summary = "重置所有進度", description = "清除用戶既所有學習進度")
    @PostMapping("/reset-all")
    public String resetAllProgress(HttpServletRequest request) {
        String sessionId = getSessionId(request);
        sessionService.deleteSession(sessionId);
        progressService.resetAllProgress(sessionId);
        return "redirect:/";
    }

    @Operation(summary = "搜尋頁面", description = "顯示搜尋結果")
    @GetMapping("/search")
    public String search(
            @Parameter(description = "搜尋關鍵字") @RequestParam String keyword,
            @Parameter(description = "頁碼") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁數量") @RequestParam(defaultValue = "10") int size,
            Model model) {
        
        // Validate page - must be non-negative
        if (page < 0) {
            page = 0;
        }
        
        // Validate size - only allow 10, 50, 100
        if (size != 10 && size != 50 && size != 100) {
            size = 10;
        }
        
        // Validate and sanitize keyword
        String sanitizedKeyword = keyword;
        if (keyword == null || keyword.trim().isEmpty()) {
            sanitizedKeyword = "";
        } else {
            sanitizedKeyword = keyword.trim();
            if (sanitizedKeyword.length() > 100) {
                sanitizedKeyword = sanitizedKeyword.substring(0, 100);
            }
        }
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<QuestionDto> results = searchService.search(sanitizedKeyword, pageable);
        
        model.addAttribute("keyword", sanitizedKeyword);
        model.addAttribute("results", results.getContent());
        model.addAttribute("currentPage", results.getNumber());
        model.addAttribute("totalPages", results.getTotalPages());
        model.addAttribute("totalElements", results.getTotalElements());
        model.addAttribute("hasPrevious", results.hasPrevious());
        model.addAttribute("hasNext", results.hasNext());
        model.addAttribute("pageSize", size);
        
        return "search";
    }
}
