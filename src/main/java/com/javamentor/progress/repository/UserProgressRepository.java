package com.javamentor.progress.repository;

import com.javamentor.progress.entity.UserProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserProgressRepository extends JpaRepository<UserProgress, Long> {

    List<UserProgress> findBySessionIdOrderByAnsweredAtDesc(@Param("sessionId") String sessionId);

    @Query("SELECT DISTINCT p FROM UserProgress p LEFT JOIN FETCH p.question q LEFT JOIN FETCH q.topic LEFT JOIN FETCH q.options WHERE p.sessionId = :sessionId AND p.isCorrect = false ORDER BY p.answeredAt DESC")
    List<UserProgress> findBySessionIdAndIsCorrectFalseOrderByAnsweredAtDesc(@Param("sessionId") String sessionId);

    @Query("SELECT COUNT(p) FROM UserProgress p WHERE p.sessionId = :sessionId")
    Long countBySessionId(@Param("sessionId") String sessionId);
    
    @Query("SELECT COUNT(p) FROM UserProgress p WHERE p.sessionId = :sessionId AND p.isCorrect = true")
    Long countCorrectBySessionId(@Param("sessionId") String sessionId);

    @Query("SELECT COUNT(p) FROM UserProgress p WHERE p.sessionId = :sessionId AND p.question.topic.topicId = :topicId")
    Long countBySessionIdAndTopicId(@Param("sessionId") String sessionId, @Param("topicId") String topicId);

    @Query("SELECT COUNT(p) FROM UserProgress p WHERE p.sessionId = :sessionId AND p.question.topic.topicId = :topicId AND p.isCorrect = true")
    Long countCorrectBySessionIdAndTopicId(@Param("sessionId") String sessionId, @Param("topicId") String topicId);

    @Query("SELECT p.question.topic.topicId, " +
           "COUNT(p), " +
           "SUM(CASE WHEN p.isCorrect = true THEN 1 ELSE 0 END) " +
           "FROM UserProgress p " +
           "WHERE p.sessionId = :sessionId " +
           "GROUP BY p.question.topic.topicId")
    List<Object[]> getTopicStatsGrouped(@Param("sessionId") String sessionId);

    void deleteBySessionId(@Param("sessionId") String sessionId);
}
