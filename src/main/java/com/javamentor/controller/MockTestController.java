package com.javamentor.controller;

import com.javamentor.mocktest.dto.AnswerRequest;
import com.javamentor.mocktest.dto.MockTestDto;
import com.javamentor.mocktest.dto.StartMockTestRequest;
import com.javamentor.question.dto.QuestionDto;
import com.javamentor.service.MockTestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/mock")
@Tag(name = "Mock Test API", description = "模擬考試 API (v1)")
public class MockTestController {
    
    private final MockTestService mockTestService;
    
    public MockTestController(MockTestService mockTestService) {
        this.mockTestService = mockTestService;
    }
    
    /**
     * POST /api/v1/mock/start - Start a new mock test
     * Body: { "topics": ["oop", "collection"], "count": 60 }
     */
    @PostMapping("/start")
    @Operation(summary = "開始模擬考試", description = "創建新的模擬考試session")
    public ResponseEntity<MockTestDto> startMockTest(@RequestBody @Valid StartMockTestRequest request) {
        MockTestDto mockTest = mockTestService.startMockTest(
            request.topics(), 
            request.count()
        );
        return ResponseEntity.ok(mockTest);
    }
    
    /**
     * GET /api/v1/mock/{sessionId}/question - Get current question
     * Returns QuestionDto instead of Entity
     */
    @GetMapping("/{sessionId}/question")
    @Operation(summary = "獲取當前題目", description = "獲取模擬考試既當前題目")
    public ResponseEntity<?> getCurrentQuestion(
            @Parameter(description = "Mock Test Session ID") @PathVariable String sessionId) {
        QuestionDto question = mockTestService.getCurrentQuestion(sessionId);
        if (question == null) {
            return ResponseEntity.ok(Map.of("completed", true));
        }
        
        // Return DTO directly - no exposure of internal entity fields
        return ResponseEntity.ok(question);
    }
    
    /**
     * POST /api/v1/mock/{sessionId}/answer - Submit answer
     * Body: { "answer": "A" }
     */
    @PostMapping("/{sessionId}/answer")
    @Operation(summary = "提交答案", description = "提交模擬考試既答案")
    public ResponseEntity<?> submitAnswer(
            @Parameter(description = "Mock Test Session ID") @PathVariable String sessionId, 
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
     * GET /api/v1/mock/{sessionId}/result - Get final result
     */
    @GetMapping("/{sessionId}/result")
    @Operation(summary = "獲取考試結果", description = "獲取模擬考試既最終結果")
    public ResponseEntity<?> getResult(
            @Parameter(description = "Mock Test Session ID") @PathVariable String sessionId) {
        Map<String, Object> result = mockTestService.getTestResult(sessionId);
        if (result == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * GET /api/v1/mock/{sessionId}/progress - Get progress
     */
    @GetMapping("/{sessionId}/progress")
    @Operation(summary = "獲取考試進度", description = "獲取模擬考試既當前進度")
    public ResponseEntity<?> getProgress(
            @Parameter(description = "Mock Test Session ID") @PathVariable String sessionId) {
        Map<String, Object> progress = mockTestService.getProgress(sessionId);
        if (progress == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(progress);
    }
}
