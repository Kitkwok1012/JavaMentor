package com.javamentor.mocktest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

/**
 * AnswerRequest - with Lombok
 */
@Data
public class AnswerRequest {
    
    @NotBlank(message = "Answer cannot be empty")
    @Pattern(regexp = "^[A-Da-d]$", message = "Answer must be A, B, C, or D")
    private String answer;
}
