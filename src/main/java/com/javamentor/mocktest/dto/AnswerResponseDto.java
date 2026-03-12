package com.javamentor.mocktest.dto;

import java.util.List;

public class AnswerResponseDto {
    private Boolean correct;
    private String correctAnswer;
    private String explanation;
    private String followUpQuestion;
    private List<String> followUpOptions;
    private Boolean hasFollowUp;
    private Boolean isLastQuestion;
    
    // Constructors
    public AnswerResponseDto() {}
    
    // Getters and Setters
    public Boolean getCorrect() { return correct; }
    public void setCorrect(Boolean correct) { this.correct = correct; }
    
    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }
    
    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
    
    public String getFollowUpQuestion() { return followUpQuestion; }
    public void setFollowUpQuestion(String followUpQuestion) { this.followUpQuestion = followUpQuestion; }
    
    public List<String> getFollowUpOptions() { return followUpOptions; }
    public void setFollowUpOptions(List<String> followUpOptions) { this.followUpOptions = followUpOptions; }
    
    public Boolean getHasFollowUp() { return hasFollowUp; }
    public void setHasFollowUp(Boolean hasFollowUp) { this.hasFollowUp = hasFollowUp; }
    
    public Boolean getIsLastQuestion() { return isLastQuestion; }
    public void setIsLastQuestion(Boolean isLastQuestion) { this.isLastQuestion = isLastQuestion; }
}
