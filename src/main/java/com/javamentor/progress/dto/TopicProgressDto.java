package com.javamentor.progress.dto;

import lombok.*;

/**
 * TopicProgressDto - with Lombok
 */
@Data @NoArgsConstructor @AllArgsConstructor
public class TopicProgressDto {
    private String topicId;
    private String topicName;
    private String description;
    private Long totalQuestions;
    private Long answeredQuestions;
    private Long correctAnswers;
    private Double accuracy;
    
    @Data @NoArgsConstructor
    public static class Builder {
        private String topicId;
        private String topicName;
        private String description;
        private Long totalQuestions;
        private Long answeredQuestions;
        private Long correctAnswers;
        private Double accuracy;
        
        public Builder topicId(String topicId) { this.topicId = topicId; return this; }
        public Builder topicName(String topicName) { this.topicName = topicName; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder totalQuestions(Long totalQuestions) { this.totalQuestions = totalQuestions; return this; }
        public Builder answeredQuestions(Long answeredQuestions) { this.answeredQuestions = answeredQuestions; return this; }
        public Builder correctAnswers(Long correctAnswers) { this.correctAnswers = correctAnswers; return this; }
        public Builder accuracy(Double accuracy) { this.accuracy = accuracy; return this; }
        
        public TopicProgressDto build() {
            TopicProgressDto dto = new TopicProgressDto();
            dto.setTopicId(topicId);
            dto.setTopicName(topicName);
            dto.setDescription(description);
            dto.setTotalQuestions(totalQuestions);
            dto.setAnsweredQuestions(answeredQuestions);
            dto.setCorrectAnswers(correctAnswers);
            dto.setAccuracy(accuracy);
            return dto;
        }
    }
}
