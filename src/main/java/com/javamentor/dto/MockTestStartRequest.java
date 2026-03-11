package com.javamentor.dto;

/**
 * DTO for starting a mock test request
 */
public class MockTestStartRequest {
    private java.util.List<String> topics;
    private Integer count = 60; // default
    
    public java.util.List<String> getTopics() {
        return topics;
    }
    
    public void setTopics(java.util.List<String> topics) {
        this.topics = topics;
    }
    
    public Integer getCount() {
        return count;
    }
    
    public void setCount(Integer count) {
        this.count = count;
    }
}
