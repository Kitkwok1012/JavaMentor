package com.javamentor.mocktest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * AnswerRequestDto - with Lombok
 */
@Data
public class AnswerRequestDto {

    @NotNull(message = "Question ID 不能為空")
    private Long questionId;

    @NotBlank(message = "答案不能為空")
    private String answer;

    @NotBlank(message = "Topic ID 不能為空")
    private String topicId;
}
