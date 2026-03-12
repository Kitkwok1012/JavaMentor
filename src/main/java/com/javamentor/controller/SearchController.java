package com.javamentor.controller;

import com.javamentor.question.dto.QuestionDto;
import com.javamentor.question.dto.SearchResultDto;
import com.javamentor.search.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@Tag(name = "Search API", description = "題目搜尋 API")
public class SearchController {
    
    private final SearchService searchService;
    
    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    /**
     * GET /api/search?keyword=xxx
     */
    @GetMapping
    @Operation(summary = "搜尋題目", description = "用關鍵字搜尋題目同解釋")
    public ResponseEntity<SearchResultDto> search(
            @Parameter(description = "搜尋關鍵字") 
            @RequestParam String keyword) {
        
        List<QuestionDto> results = searchService.search(keyword);
        return ResponseEntity.ok(new SearchResultDto(keyword, results));
    }

    /**
     * GET /api/search/topic/{topicId}?keyword=xxx
     */
    @GetMapping("/topic/{topicId}")
    @Operation(summary = "喺特定 Topic 搜尋", description = "用關鍵字喺特定 Topic 度搜尋")
    public ResponseEntity<SearchResultDto> searchByTopic(
            @Parameter(description = "搜尋關鍵字") 
            @RequestParam String keyword,
            @Parameter(description = "Topic ID") 
            @PathVariable String topicId) {
        
        List<QuestionDto> results = searchService.searchByTopic(keyword, topicId);
        return ResponseEntity.ok(new SearchResultDto(keyword, results));
    }
}
