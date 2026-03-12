package com.javamentor.question.dto;

import lombok.*;

import java.util.List;

/**
 * Question DTO - with normalized options
 */
@Data @NoArgsConstructor @AllArgsConstructor
public class QuestionDto {
    private Long id;
    private String topicId;
    private String topicName;
    private String question;
    private List<OptionDto> options;
    private Boolean multiSelect;
    private Integer difficulty;
    
    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class OptionDto {
        private String label;
        private String content;
    }
}
