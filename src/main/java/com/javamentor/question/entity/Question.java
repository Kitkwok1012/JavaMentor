package com.javamentor.question.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Question Entity - with Lombok for minimal boilerplate
 */
@Entity
@Table(name = "questions", indexes = {
    @Index(name = "idx_question_topic_id", columnList = "topic_id"),
    @Index(name = "idx_question_difficulty", columnList = "difficulty"),
    @Index(name = "idx_question_display_order", columnList = "display_order")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Question {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String question;
    
    @Column(columnDefinition = "TEXT")
    private String optionA;
    
    @Column(columnDefinition = "TEXT")
    private String optionB;
    
    @Column(columnDefinition = "TEXT")
    private String optionC;
    
    @Column(columnDefinition = "TEXT")
    private String optionD;
    
    @Column(columnDefinition = "TEXT")
    private String optionE;
    
    @Column(nullable = false)
    private String correctAnswer;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean multiSelect = false;
    
    @Column(columnDefinition = "TEXT")
    private String explanation;
    
    @Column(columnDefinition = "TEXT")
    private String followUpCorrect;
    
    @Column(columnDefinition = "TEXT")
    private String followUpWrong;
    
    private Integer difficulty;
    
    private Integer displayOrder;
    
    private String tags;
}
