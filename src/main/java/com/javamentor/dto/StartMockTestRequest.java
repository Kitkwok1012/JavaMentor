package com.javamentor.dto;

/**
 * DTO for starting a mock test request (Java Record)
 */
public record StartMockTestRequest(
    java.util.List<String> topics,
    Integer count
) {
    /**
     * Constructor with default count
     */
    public StartMockTestRequest {
        if (count == null || count < 10 || count > 90) {
            count = 60;
        }
    }
    
    /**
     * Convenience constructor
     */
    public StartMockTestRequest(java.util.List<String> topics) {
        this(topics, 60);
    }
}
