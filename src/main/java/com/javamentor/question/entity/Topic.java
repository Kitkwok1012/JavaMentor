package com.javamentor.question.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Topic Entity - normalized, no denormalized counts
 */
@Entity
@Table(name = "topics")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Topic {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String topicId;
    
    @Column(nullable = false)
    private String name;
    
    private String description;
    
    private LocalDateTime createdAt;
}
