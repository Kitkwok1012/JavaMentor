package com.javamentor.controller;

import com.javamentor.config.SessionFilter;
import com.javamentor.dto.*;
import com.javamentor.entity.*;
import com.javamentor.service.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
public class LearnController {

    private static final Logger log = LoggerFactory.getLogger(LearnController.class);

    private final QuestionService questionService;

    public LearnController(QuestionService questionService) {
        this.questionService = questionService;
    }

    private String getSessionId(HttpServletRequest request) {
        return (String) request.getAttribute(SessionFilter.SESSION_ATTRIBUTE);
    }

    @GetMapping("/")
    public String index(Model model, HttpServletRequest request) {
        String sessionId = getSessionId(request);
        List<TopicProgressDto> progress = questionService.getTopicProgress(sessionId);
        Map<String, Object> stats = questionService.getUserStats(sessionId);
        
        model.addAttribute("progress", progress);
        model.addAttribute("stats", stats);
        return "index";
    }

    @GetMapping("/learn/{topicId}")
    public String learnTopic(@PathVariable String topicId, Model model, HttpServletRequest request) {
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

    @GetMapping("/question/next")
    public String getNextQuestion(@RequestParam String topicId, Model model, HttpServletRequest request) {
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

    @PostMapping("/answer")
    public String submitAnswer(
            @RequestParam Long questionId,
            @RequestParam String answer,
            @RequestParam String topicId,
            Model model,
            HttpServletRequest request) {

        String sessionId = getSessionId(request);
        
        log.info("Submitting answer: sessionId={}, questionId={}, answer={}, topicId={}", 
                sessionId, questionId, answer, topicId);

        // Validate inputs
        if (questionId == null || answer == null || topicId == null) {
            model.addAttribute("error", "請填寫所有必填項目");
            return "redirect:/learn/" + topicId;
        }

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

    @GetMapping("/wrong")
    public String wrongQuestions(Model model, HttpServletRequest request) {
        String sessionId = getSessionId(request);
        List<UserProgress> wrongQuestions = questionService.getWrongQuestions(sessionId);
        model.addAttribute("wrongQuestions", wrongQuestions);
        return "wrong";
    }

    @GetMapping("/progress")
    public String progress(Model model, HttpServletRequest request) {
        String sessionId = getSessionId(request);
        List<TopicProgressDto> progress = questionService.getTopicProgress(sessionId);
        Map<String, Object> stats = questionService.getUserStats(sessionId);
        
        model.addAttribute("progress", progress);
        model.addAttribute("stats", stats);
        return "progress";
    }

    @PostMapping("/learn/{topicId}/reset")
    public String resetTopic(@PathVariable String topicId, HttpServletRequest request) {
        String sessionId = getSessionId(request);
        questionService.resetTopic(sessionId, topicId);
        return "redirect:/learn/" + topicId;
    }

    /**
     * Reset ALL progress for current user
     */
    @PostMapping("/reset-all")
    public String resetAllProgress(HttpServletRequest request) {
        String sessionId = getSessionId(request);
        questionService.resetAllProgress(sessionId);
        return "redirect:/";
    }

    @GetMapping("/question/related")
    @ResponseBody
    public List<QuestionDto> getRelatedQuestions(@RequestParam Long questionId,
                                                  @RequestParam boolean correct) {
        return questionService.findRelatedQuestions(questionId, correct);
    }

    // Follow-up recommendation endpoint
    @GetMapping("/api/recommend")
    @ResponseBody
    public QuestionDto recommendNext(@RequestParam Long questionId,
                                    @RequestParam boolean correct,
                                    HttpServletRequest request) {
        String sessionId = getSessionId(request);
        return questionService.recommendNextQuestion(sessionId, questionId, correct);
    }
}
