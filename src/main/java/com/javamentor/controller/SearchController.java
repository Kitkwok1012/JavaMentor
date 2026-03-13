package com.javamentor.controller;

import com.javamentor.question.dto.PagedSearchResultDto;
import com.javamentor.question.dto.QuestionDto;
import com.javamentor.question.dto.SearchResultDto;
import com.javamentor.search.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/search")
@Tag(name = "Search API", description = "題目搜尋 API")
public class SearchController {
    
    private final SearchService searchService;
    
    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    /**
     * GET /api/search?keyword=xxx&page=0&size=10
     */
    @GetMapping
    @Operation(summary = "搜尋題目", description = "用關鍵字搜尋題目同解釋（分頁）")
    public ResponseEntity<PagedSearchResultDto> search(
            @Parameter(description = "搜尋關鍵字") 
            @RequestParam String keyword,
            @Parameter(description = "頁碼 (0-based)") 
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁數量") 
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<QuestionDto> results = searchService.search(keyword, pageable);
        return ResponseEntity.ok(new PagedSearchResultDto(keyword, results));
    }

    /**
     * GET /api/search/topic/{topicId}?keyword=xxx&page=0&size=10
     */
    @GetMapping("/topic/{topicId}")
    @Operation(summary = "喺特定 Topic 搜尋", description = "用關鍵字喺特定 Topic 度搜尋（分頁）")
    public ResponseEntity<PagedSearchResultDto> searchByTopic(
            @Parameter(description = "搜尋關鍵字") 
            @RequestParam String keyword,
            @Parameter(description = "Topic ID") 
            @PathVariable String topicId,
            @Parameter(description = "頁碼 (0-based)") 
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁數量") 
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<QuestionDto> results = searchService.searchByTopic(keyword, topicId, pageable);
        return ResponseEntity.ok(new PagedSearchResultDto(keyword, results));
    }
}
