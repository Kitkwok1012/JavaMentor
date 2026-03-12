package com.javamentor.question.dto;

import java.util.List;

/**
 * Search response DTO
 */
public class SearchResultDto {
    private String keyword;
    private List<QuestionDto> results;
    private int totalResults;
    private String searchedAt;
    
    public SearchResultDto() {}
    
    public SearchResultDto(String keyword, List<QuestionDto> results) {
        this.keyword = keyword;
        this.results = results;
        this.totalResults = results.size();
        this.searchedAt = java.time.LocalDateTime.now().toString();
    }
    
    // Getters and Setters
    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
    
    public List<QuestionDto> getResults() { return results; }
    public void setResults(List<QuestionDto> results) { this.results = results; }
    
    public int getTotalResults() { return totalResults; }
    public void setTotalResults(int totalResults) { this.totalResults = totalResults; }
    
    public String getSearchedAt() { return searchedAt; }
    public void setSearchedAt(String searchedAt) { this.searchedAt = searchedAt; }
}
