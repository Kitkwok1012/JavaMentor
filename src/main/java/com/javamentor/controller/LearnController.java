package com.javamentor.controller;

import com.javamentor.dto.*;
import com.javamentor.entity.*;
import com.javamentor.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class LearnController {
    
    private final QuestionService questionService;
    
    public LearnController(QuestionService questionService) {
        this.questionService = questionService;
    }
    
    @GetMapping("/")
    public String index(Model model) {
        List<TopicProgressDto> progress = questionService.getTopicProgress();
        
        model.addAttribute("progress", progress);
        
        return "index";
    }
    
    @GetMapping("/learn/{topicId}")
    public String learnTopic(@PathVariable String topicId, Model model) {
        Topic topic = questionService.getTopicById(topicId);
        if (topic == null) {
            return "redirect:/";
        }
        
        QuestionDto question = questionService.getNextQuestion(topicId);
        
        model.addAttribute("topic", topic);
        model.addAttribute("question", question);
        
        if (question == null) {
            model.addAttribute("completed", true);
            return "learn";
        }
        
        return "learn";
    }
    
    @GetMapping("/question/next")
    public String getNextQuestion(@RequestParam String topicId, Model model) {
        // Increment index and get next question
        questionService.moveToNextQuestion(topicId);
        
        QuestionDto question = questionService.getNextQuestion(topicId);
        Topic topic = questionService.getTopicById(topicId);
        
        model.addAttribute("topic", topic);
        model.addAttribute("question", question);
        
        if (question == null) {
            model.addAttribute("completed", true);
        }
        
        return "learn";
    }
    
    @PostMapping("/answer")
    public String submitAnswer(@RequestParam Long questionId,
                              @RequestParam String answer,
                              @RequestParam String topicId,
                              Model model) {
        
        // Get question info for display (the question that was just answered)
        QuestionDto question = questionService.getQuestionById(questionId);
        AnswerResponseDto response = questionService.submitAnswer(questionId, answer);
        Topic topic = questionService.getTopicById(topicId);
        
        model.addAttribute("topic", topic);
        model.addAttribute("question", question);  // Show the answered question
        model.addAttribute("response", response);  // Show the answer result
        model.addAttribute("questionId", questionId);
        model.addAttribute("topicId", topicId);
        
        return "learn";
    }
    
    @GetMapping("/wrong")
    public String wrongQuestions(Model model) {
        List<UserProgress> wrongQuestions = questionService.getWrongQuestions();
        model.addAttribute("wrongQuestions", wrongQuestions);
        return "wrong";
    }
    
    @GetMapping("/progress")
    public String progress(Model model) {
        List<TopicProgressDto> progress = questionService.getTopicProgress();
        model.addAttribute("progress", progress);
        return "progress";
    }
    
    @PostMapping("/learn/{topicId}/reset")
    public String resetTopic(@PathVariable String topicId) {
        questionService.resetTopic(topicId);
        return "redirect:/learn/" + topicId;
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
                                    @RequestParam boolean correct) {
        return questionService.recommendNextQuestion(questionId, correct);
    }
}
