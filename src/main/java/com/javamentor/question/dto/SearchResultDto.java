package com.javamentor.question.dto;

import lombok.*;
import java.util.List;

/**
 * SearchResultDto - with Lombok
 */
@Getter @Setter @NoArgsConstructor
public class SearchResultDto {
    private String keyword;
    private List<QuestionDto> results;
    private int totalResults;
    private String searchedAt;
    
    public SearchResultDto(String keyword, List<QuestionDto> results) {
        this.keyword = keyword;
        this.results = results;
        this.totalResults = results != null ? results.size() : 0;
        this.searchedAt = java.time.LocalDateTime.now().toString();
    }
}
