package com.javamentor.repository;

import com.javamentor.entity.Question;
import com.javamentor.entity.UserProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserProgressRepository extends JpaRepository<UserProgress, Long> {
    
    List<UserProgress> findByIsCorrectFalseOrderByAnsweredAtDesc();
    
    List<UserProgress> findAllByOrderByAnsweredAtDesc();
    
    @Query("SELECT COUNT(u) FROM UserProgress u WHERE u.question.topic.topicId = :topicId AND u.isCorrect = true")
    Long countCorrectByTopicId(String topicId);
    
    @Query("SELECT COUNT(u) FROM UserProgress u WHERE u.question.topic.topicId = :topicId")
    Long countByTopicId(String topicId);
    
    boolean existsByQuestion(Question question);
}
