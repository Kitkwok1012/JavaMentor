package com.javamentor.question.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Question Option Entity - normalized design
 * Supports unlimited options (A, B, C, D, E, F, etc.)
 */
@Entity
@Table(name = "question_options")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class QuestionOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(nullable = false, length = 5)
    private String label;  // "A", "B", "C", "D", "E", "F"...

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isCorrect = false;
}
