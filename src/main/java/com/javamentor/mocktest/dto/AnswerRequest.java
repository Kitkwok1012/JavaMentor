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
    @Pattern(regexp = "^([A-Ea-e](,[A-Ea-e])*)$", message = "Answer must be A-E, comma-separated for multi-select (e.g., A,C)")
    private String answer;
}
