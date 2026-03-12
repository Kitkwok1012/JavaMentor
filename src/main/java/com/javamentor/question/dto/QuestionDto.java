package com.javamentor.question.dto;

import lombok.*;

/**
 * Question DTO - with Lombok
 */
@Data @NoArgsConstructor @AllArgsConstructor
public class QuestionDto {
    private Long id;
    private String topicId;
    private String topicName;
    private String question;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String optionE;
    private Boolean multiSelect;
    private Integer difficulty;
}
