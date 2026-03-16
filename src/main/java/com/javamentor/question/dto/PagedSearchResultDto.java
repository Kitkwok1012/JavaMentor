package com.javamentor.question.dto;

import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Paginated Search Result DTO
 */
@Getter @Setter @NoArgsConstructor
public class PagedSearchResultDto {
    private String keyword;
    private List<QuestionDto> results;
    private int currentPage;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean hasNext;
    private boolean hasPrevious;
    private String searchedAt;
    
    public PagedSearchResultDto(String keyword, Page<QuestionDto> page) {
        this.keyword = keyword;
        this.results = page.getContent();
        this.currentPage = page.getNumber();
        this.pageSize = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.hasNext = page.hasNext();
        this.hasPrevious = page.hasPrevious();
        this.searchedAt = java.time.LocalDateTime.now().toString();
    }
}
