package com.javamentor.repository;

import com.javamentor.entity.UserProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserProgressRepository extends JpaRepository<UserProgress, Long> {

    List<UserProgress> findBySessionIdOrderByAnsweredAtDesc(String sessionId);

    List<UserProgress> findBySessionIdAndIsCorrectFalseOrderByAnsweredAtDesc(String sessionId);

    @Query("SELECT COUNT(p) FROM UserProgress p WHERE p.sessionId = :sessionId")
    Long countBySessionId(String sessionId);
    
    @Query("SELECT COUNT(p) FROM UserProgress p WHERE p.sessionId = :sessionId AND p.isCorrect = true")
    Long countCorrectBySessionId(String sessionId);

    @Query("SELECT COUNT(p) FROM UserProgress p WHERE p.sessionId = :sessionId AND p.question.topic.topicId = :topicId")
    Long countBySessionIdAndTopicId(String sessionId, String topicId);

    @Query("SELECT COUNT(p) FROM UserProgress p WHERE p.sessionId = :sessionId AND p.question.topic.topicId = :topicId AND p.isCorrect = true")
    Long countCorrectBySessionIdAndTopicId(String sessionId, String topicId);

    @Query("SELECT p.question.topic.topicId, " +
           "COUNT(p), " +
           "SUM(CASE WHEN p.isCorrect = true THEN 1 ELSE 0 END) " +
           "FROM UserProgress p " +
           "WHERE p.sessionId = :sessionId " +
           "GROUP BY p.question.topic.topicId")
    List<Object[]> getTopicStatsGrouped(String sessionId);

    void deleteBySessionId(String sessionId);
}
