package com.javamentor.dto;

public class TopicProgressDto {
    private String topicId;
    private String topicName;
    private String description;
    private Long totalQuestions;
    private Long answeredQuestions;
    private Long correctAnswers;
    private Double accuracy;
    
    // Constructors
    public TopicProgressDto() {}
    
    public TopicProgressDto(String topicId, String topicName, Long totalQuestions, Long answeredQuestions, Long correctAnswers, Double accuracy) {
        this.topicId = topicId;
        this.topicName = topicName;
        this.totalQuestions = totalQuestions;
        this.answeredQuestions = answeredQuestions;
        this.correctAnswers = correctAnswers;
        this.accuracy = accuracy;
    }
    
    // Getters and Setters
    public String getTopicId() { return topicId; }
    public void setTopicId(String topicId) { this.topicId = topicId; }
    
    public String getTopicName() { return topicName; }
    public void setTopicName(String topicName) { this.topicName = topicName; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Long getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(Long totalQuestions) { this.totalQuestions = totalQuestions; }
    
    public Long getAnsweredQuestions() { return answeredQuestions; }
    public void setAnsweredQuestions(Long answeredQuestions) { this.answeredQuestions = answeredQuestions; }
    
    public Long getCorrectAnswers() { return correctAnswers; }
    public void setCorrectAnswers(Long correctAnswers) { this.correctAnswers = correctAnswers; }
    
    public Double getAccuracy() { return accuracy; }
    public void setAccuracy(Double accuracy) { this.accuracy = accuracy; }
}
