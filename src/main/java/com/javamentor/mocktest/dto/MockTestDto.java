package com.javamentor.mocktest.dto;

import java.util.List;

public class MockTestDto {
    private String sessionId;
    private List<Long> questionIds;
    private int totalQuestions;
    private int currentIndex;
    private String selectedTopic;
    private List<String> selectedTopics;
    private Long startTime;
    
    // Getters and Setters
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    
    public List<Long> getQuestionIds() { return questionIds; }
    public void setQuestionIds(List<Long> questionIds) { this.questionIds = questionIds; }
    
    public int getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }
    
    public int getCurrentIndex() { return currentIndex; }
    public void setCurrentIndex(int currentIndex) { this.currentIndex = currentIndex; }
    
    public String getSelectedTopic() { return selectedTopic; }
    public void setSelectedTopic(String selectedTopic) { this.selectedTopic = selectedTopic; }
    
    public List<String> getSelectedTopics() { return selectedTopics; }
    public void setSelectedTopics(List<String> selectedTopics) { this.selectedTopics = selectedTopics; }
    
    public Long getStartTime() { return startTime; }
    public void setStartTime(Long startTime) { this.startTime = startTime; }
}
