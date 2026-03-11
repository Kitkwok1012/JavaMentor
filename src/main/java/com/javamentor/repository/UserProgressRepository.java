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

    @Query("SELECT COUNT(p) FROM UserProgress p WHERE p.sessionId = :sessionId AND p.question.topic.topicId = :topicId")
    Long countBySessionIdAndTopicId(String sessionId, String topicId);

    @Query("SELECT COUNT(p) FROM UserProgress p WHERE p.sessionId = :sessionId AND p.question.topic.topicId = :topicId AND p.isCorrect = true")
    Long countCorrectBySessionIdAndTopicId(String sessionId, String topicId);

    @Query("SELECT p FROM UserProgress p WHERE p.sessionId = :sessionId ORDER BY p.answeredAt DESC")
List<UserProgress> findAllBySessionIdOrderByAnsweredAtDesc(String sessionId);

    void deleteBySessionId(String sessionId);
}
