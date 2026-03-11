package com.javamentor.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "questions", indexes = {
    @Index(name = "idx_question_topic_id", columnList = "topic_id"),
    @Index(name = "idx_question_difficulty", columnList = "difficulty"),
    @Index(name = "idx_question_display_order", columnList = "display_order")
})
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
    
    // Constructors
    public Question() {}
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Topic getTopic() { return topic; }
    public void setTopic(Topic topic) { this.topic = topic; }
    
    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
    
    public String getOptionA() { return optionA; }
    public void setOptionA(String optionA) { this.optionA = optionA; }
    
    public String getOptionB() { return optionB; }
    public void setOptionB(String optionB) { this.optionB = optionB; }
    
    public String getOptionC() { return optionC; }
    public void setOptionC(String optionC) { this.optionC = optionC; }
    
    public String getOptionD() { return optionD; }
    public void setOptionD(String optionD) { this.optionD = optionD; }
    
    public String getOptionE() { return optionE; }
    public void setOptionE(String optionE) { this.optionE = optionE; }
    
    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }
    
    public Boolean getMultiSelect() { return multiSelect; }
    public void setMultiSelect(Boolean multiSelect) { this.multiSelect = multiSelect; }
    
    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
    
    public String getFollowUpCorrect() { return followUpCorrect; }
    public void setFollowUpCorrect(String followUpCorrect) { this.followUpCorrect = followUpCorrect; }
    
    public String getFollowUpWrong() { return followUpWrong; }
    public void setFollowUpWrong(String followUpWrong) { this.followUpWrong = followUpWrong; }
    
    public Integer getDifficulty() { return difficulty; }
    public void setDifficulty(Integer difficulty) { this.difficulty = difficulty; }
    
    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
    
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
}
