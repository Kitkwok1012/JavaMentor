package com.javamentor.mocktest.dto;

import lombok.*;
import java.util.List;

/**
 * AnswerResponseDto - with Lombok
 */
@Data @NoArgsConstructor @AllArgsConstructor
public class AnswerResponseDto {
    private Boolean correct;
    private String correctAnswer;
    private String explanation;
    private String followUpQuestion;
    private List<String> followUpOptions;
    private Boolean hasFollowUp;
    private Boolean isLastQuestion;
}
