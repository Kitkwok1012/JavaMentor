package com.javamentor.mocktest.dto;

import lombok.*;
import java.util.List;

/**
 * MockTestDto - with Lombok
 */
@Data @NoArgsConstructor @AllArgsConstructor
public class MockTestDto {
    private String sessionId;
    private List<Long> questionIds;
    private int totalQuestions;
    private int currentIndex;
    private String selectedTopic;
    private List<String> selectedTopics;
    private Long startTime;
}
