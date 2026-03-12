package com.javamentor.session.repository;

import com.javamentor.session.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Long> {
    
    Optional<UserSession> findBySessionId(String sessionId);
    
    Optional<UserSession> findBySessionIdAndTopicId(String sessionId, String topicId);
    
    List<UserSession> findBySessionIdOrderByCreatedAtDesc(String sessionId);
    
    void deleteBySessionId(String sessionId);
}
