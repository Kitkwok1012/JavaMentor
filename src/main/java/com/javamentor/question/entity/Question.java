package com.javamentor.question.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Question Entity - with normalized options
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
    
    // Normalized options - supports unlimited options
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("label ASC")
    @Builder.Default
    private List<QuestionOption> options = new ArrayList<>();
    
    @Column(columnDefinition = "TEXT")
    private String explanation;
    
    @Column(columnDefinition = "TEXT")
    private String followUpCorrect;
    
    @Column(columnDefinition = "TEXT")
    private String followUpWrong;
    
    private Integer difficulty;
    
    private Integer displayOrder;
    
    private String tags;
    
    /**
     * Helper method to add an option
     */
    public void addOption(String label, String content, boolean isCorrect) {
        QuestionOption option = QuestionOption.builder()
                .label(label)
                .content(content)
                .isCorrect(isCorrect)
                .build();
        option.setQuestion(this);
        this.options.add(option);
    }
    
    /**
     * Get correct answer as string (e.g., "A,C")
     */
    public String getCorrectAnswer() {
        return options.stream()
                .filter(QuestionOption::getIsCorrect)
                .map(QuestionOption::getLabel)
                .reduce((a, b) -> a + "," + b)
                .orElse("");
    }
    
    /**
     * Check if this is a multi-select question
     */
    public Boolean getMultiSelect() {
        long correctCount = options.stream().filter(QuestionOption::getIsCorrect).count();
        return correctCount > 1;
    }
}
