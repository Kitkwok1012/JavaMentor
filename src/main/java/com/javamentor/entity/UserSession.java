package com.javamentor.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_sessions")
public class UserSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String sessionId;  // Cookie value (UUID)

    @Column(nullable = false)
    private String topicId;

    @Column(nullable = false)
    private String questionOrder;  // JSON array of question IDs in order

    private Integer currentIndex;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public String getTopicId() { return topicId; }
    public void setTopicId(String topicId) { this.topicId = topicId; }

    public String getQuestionOrder() { return questionOrder; }
    public void setQuestionOrder(String questionOrder) { this.questionOrder = questionOrder; }

    public Integer getCurrentIndex() { return currentIndex; }
    public void setCurrentIndex(Integer currentIndex) { this.currentIndex = currentIndex; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
