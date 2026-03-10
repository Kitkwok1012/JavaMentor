package com.javamentor.controller;

import com.javamentor.dto.MockTestDto;
import com.javamentor.entity.Question;
import com.javamentor.service.MockTestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mock")
public class MockTestController {
    
    private final MockTestService mockTestService;
    
    public MockTestController(MockTestService mockTestService) {
        this.mockTestService = mockTestService;
    }
    
    /**
     * POST /api/mock/start - Start a new mock test
     * Body: { "topics": ["oop", "collection"], "count": 60 }
     */
    @PostMapping("/start")
    public ResponseEntity<MockTestDto> startMockTest(@RequestBody Map<String, Object> request) {
        @SuppressWarnings("unchecked")
        List<String> topics = (List<String>) request.get("topics");
        Integer count = (Integer) request.getOrDefault("count", 60);
        
        MockTestDto mockTest = mockTestService.startMockTest(topics, count);
        return ResponseEntity.ok(mockTest);
    }
    
    /**
     * GET /api/mock/{sessionId}/question - Get current question
     */
    @GetMapping("/{sessionId}/question")
    public ResponseEntity<?> getCurrentQuestion(@PathVariable String sessionId) {
        Question question = mockTestService.getCurrentQuestion(sessionId);
        if (question == null) {
            return ResponseEntity.ok(Map.of("completed", true));
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("id", question.getId());
        response.put("question", question.getQuestion());
        response.put("optionA", question.getOptionA());
        response.put("optionB", question.getOptionB());
        response.put("optionC", question.getOptionC());
        response.put("optionD", question.getOptionD());
        response.put("optionE", question.getOptionE());
        response.put("multiSelect", question.getMultiSelect());
        response.put("difficulty", question.getDifficulty());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * POST /api/mock/{sessionId}/answer - Submit answer
     * Body: { "answer": "A" }
     */
    @PostMapping("/{sessionId}/answer")
    public ResponseEntity<?> submitAnswer(@PathVariable String sessionId, @RequestBody Map<String, String> request) {
        String answer = request.get("answer");
        boolean isCorrect = mockTestService.submitAnswer(sessionId, answer);
        
        Map<String, Object> response = new HashMap<>();
        response.put("correct", isCorrect);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET /api/mock/{sessionId}/result - Get final result
     */
    @GetMapping("/{sessionId}/result")
    public ResponseEntity<?> getResult(@PathVariable String sessionId) {
        Map<String, Object> result = mockTestService.getTestResult(sessionId);
        if (result == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * GET /api/mock/{sessionId}/progress - Get progress
     */
    @GetMapping("/{sessionId}/progress")
    public ResponseEntity<?> getProgress(@PathVariable String sessionId) {
        Map<String, Object> progress = mockTestService.getProgress(sessionId);
        if (progress == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(progress);
    }
}
