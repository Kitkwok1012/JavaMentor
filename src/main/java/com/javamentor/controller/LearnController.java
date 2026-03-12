package com.javamentor.controller;

import com.javamentor.config.SessionFilter;
import com.javamentor.mocktest.dto.AnswerResponseDto;
import com.javamentor.progress.dto.TopicProgressDto;
import com.javamentor.progress.entity.UserProgress;
import com.javamentor.question.dto.QuestionDto;
import com.javamentor.question.entity.Question;
import com.javamentor.question.entity.Topic;
import com.javamentor.service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@Tag(name = "Learn Controller", description = "題目學習與答題 API")
public class LearnController {

    private static final Logger log = LoggerFactory.getLogger(LearnController.class);

    private final QuestionService questionService;

    public LearnController(QuestionService questionService) {
        this.questionService = questionService;
    }

    private String getSessionId(HttpServletRequest request) {
        return (String) request.getAttribute(SessionFilter.SESSION_ATTRIBUTE);
    }

    @Operation(summary = "首頁", description = "獲取首頁數據，包含學習進度和統計")
    @GetMapping("/")
    public String index(Model model, HttpServletRequest request) {
        String sessionId = getSessionId(request);
        List<TopicProgressDto> progress = questionService.getTopicProgress(sessionId);
        Map<String, Object> stats = questionService.getUserStats(sessionId);
        
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
        
        Topic topic;
        try {
            topic = questionService.getTopicById(topicId);
        } catch (Exception e) {
            log.warn("Topic not found: {}", topicId);
            return "redirect:/";
        }

        QuestionDto question = questionService.getNextQuestion(sessionId, topicId);

        model.addAttribute("topic", topic);
        model.addAttribute("question", question);

        if (question == null) {
            model.addAttribute("completed", true);
            return "learn";
        }

        return "learn";
    }

    @Operation(summary = "下一題", description = "移動到 Topic 既下一題")
    @GetMapping("/question/next")
    public String getNextQuestion(
            @Parameter(description = "Topic ID") @RequestParam String topicId, 
            Model model, HttpServletRequest request) {
        String sessionId = getSessionId(request);
        
        questionService.moveToNextQuestion(sessionId, topicId);

        QuestionDto question = questionService.getNextQuestion(sessionId, topicId);
        Topic topic = questionService.getTopicById(topicId);

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
            @Parameter(description = "Question ID") @RequestParam Long questionId,
            @Parameter(description = "答案 (A/B/C/D)") @RequestParam String answer,
            @Parameter(description = "Topic ID") @RequestParam String topicId,
            Model model,
            HttpServletRequest request) {

        String sessionId = getSessionId(request);
        
        log.info("Submitting answer: sessionId={}, questionId={}, answer={}, topicId={}", 
                sessionId, questionId, answer, topicId);

        try {
            QuestionDto question = questionService.getQuestionById(questionId);
            AnswerResponseDto response = questionService.submitAnswer(sessionId, questionId, answer);
            Topic topic = questionService.getTopicById(topicId);

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
        List<UserProgress> wrongQuestions = questionService.getWrongQuestions(sessionId);
        model.addAttribute("wrongQuestions", wrongQuestions);
        return "wrong";
    }

    @Operation(summary = "學習進度", description = "查看 overall learning progress")
    @GetMapping("/progress")
    public String progress(Model model, HttpServletRequest request) {
        String sessionId = getSessionId(request);
        List<TopicProgressDto> progress = questionService.getTopicProgress(sessionId);
        Map<String, Object> stats = questionService.getUserStats(sessionId);
        
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
        questionService.resetTopic(sessionId, topicId);
        return "redirect:/learn/" + topicId;
    }

    @Operation(summary = "重置所有進度", description = "清除用戶既所有學習進度")
    @PostMapping("/reset-all")
    public String resetAllProgress(HttpServletRequest request) {
        String sessionId = getSessionId(request);
        questionService.resetAllProgress(sessionId);
        return "redirect:/";
    }

    @Operation(summary = "相關題目", description = "獲取與當前題目相關既其他題目")
    @GetMapping("/question/related")
    @ResponseBody
    public List<QuestionDto> getRelatedQuestions(
            @Parameter(description = "Question ID") @RequestParam Long questionId,
            @Parameter(description = "是否答啱") @RequestParam boolean correct) {
        return questionService.findRelatedQuestions(questionId, correct);
    }

    @Operation(summary = "智能推薦", description = "根據當前答題情況推薦下一題")
    @GetMapping("/api/recommend")
    @ResponseBody
    public QuestionDto recommendNext(
            @Parameter(description = "Question ID") @RequestParam Long questionId,
            @Parameter(description = "是否答啱") @RequestParam boolean correct,
            HttpServletRequest request) {
        String sessionId = getSessionId(request);
        return questionService.recommendNextQuestion(sessionId, questionId, correct);
    }
}
