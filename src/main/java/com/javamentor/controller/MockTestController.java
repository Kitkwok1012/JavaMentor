package com.javamentor.controller;

import com.javamentor.mocktest.dto.AnswerRequest;
import com.javamentor.mocktest.dto.MockTestDto;
import com.javamentor.mocktest.dto.StartMockTestRequest;
import com.javamentor.question.dto.QuestionDto;
import com.javamentor.service.MockTestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import jakarta.validation.Valid;

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
    public ResponseEntity<MockTestDto> startMockTest(@RequestBody @Valid StartMockTestRequest request) {
        MockTestDto mockTest = mockTestService.startMockTest(
            request.topics(), 
            request.count()
        );
        return ResponseEntity.ok(mockTest);
    }
    
    /**
     * GET /api/mock/{sessionId}/question - Get current question
     * Returns QuestionDto instead of Entity
     */
    @GetMapping("/{sessionId}/question")
    public ResponseEntity<?> getCurrentQuestion(@PathVariable String sessionId) {
        QuestionDto question = mockTestService.getCurrentQuestion(sessionId);
        if (question == null) {
            return ResponseEntity.ok(Map.of("completed", true));
        }
        
        // Return DTO directly - no exposure of internal entity fields
        return ResponseEntity.ok(question);
    }
    
    /**
     * POST /api/mock/{sessionId}/answer - Submit answer
     * Body: { "answer": "A" }
     */
    @PostMapping("/{sessionId}/answer")
    public ResponseEntity<?> submitAnswer(
            @PathVariable String sessionId, 
            @RequestBody @Valid AnswerRequest request) {
        String answer = request.getAnswer();
        
        // Get result including correct answer for feedback
        Map<String, Object> result = mockTestService.submitAnswerWithFeedback(sessionId, answer);
        
        if (result == null) {
            return ResponseEntity.ok(Map.of("completed", true));
        }
        
        return ResponseEntity.ok(result);
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
