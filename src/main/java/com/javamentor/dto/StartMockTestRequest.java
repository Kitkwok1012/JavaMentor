package com.javamentor.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * DTO for starting a mock test request (Java Record)
 */
public record StartMockTestRequest(
    java.util.List<String> topics,
    @Min(value = 10, message = "count must be at least 10")
    @Max(value = 90, message = "count must be at most 90")
    Integer count
) {
    /**
     * Constructor with default count when null
     */
    public StartMockTestRequest {
        if (count == null) count = 60;
    }
    
    /**
     * Convenience constructor
     */
    public StartMockTestRequest(java.util.List<String> topics) {
        this(topics, 60);
    }
}
