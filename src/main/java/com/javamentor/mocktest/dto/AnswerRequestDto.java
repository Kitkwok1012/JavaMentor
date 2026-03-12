package com.javamentor.mocktest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AnswerRequestDto {

    @NotNull(message = "Question ID 不能為空")
    private Long questionId;

    @NotBlank(message = "答案不能為空")
    private String answer;

    @NotBlank(message = "Topic ID 不能為空")
    private String topicId;

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }
}
