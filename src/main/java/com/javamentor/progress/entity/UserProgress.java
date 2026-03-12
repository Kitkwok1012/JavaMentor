package com.javamentor.progress.entity;

import com.javamentor.question.entity.Question;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * UserProgress Entity - with Lombok
 */
@Entity
@Table(name = "user_progress", indexes = {
    @Index(name = "idx_progress_session_id", columnList = "session_id"),
    @Index(name = "idx_progress_question_id", columnList = "question_id"),
    @Index(name = "idx_progress_is_correct", columnList = "is_correct"),
    @Index(name = "idx_progress_answered_at", columnList = "answered_at")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String sessionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(nullable = false)
    private String userAnswer;

    @Column(nullable = false)
    private Boolean isCorrect;

    private LocalDateTime answeredAt;

    @PrePersist
    protected void onCreate() {
        answeredAt = LocalDateTime.now();
    }
}
