package com.javamentor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * DTO for submitting mock test answer
 */
public class AnswerRequest {
    
    @NotBlank(message = "Answer cannot be empty")
    @Pattern(regexp = "^[A-Da-d]$", message = "Answer must be A, B, C, or D")
    private String answer;

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
