package com.javamentor.session.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javamentor.question.entity.Question;
import com.javamentor.question.service.QuestionService;
import com.javamentor.session.entity.UserSession;
import com.javamentor.session.repository.UserSessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Session Service - 負責用戶會話管理
 */
@Service
public class SessionService {

    private static final Logger log = LoggerFactory.getLogger(SessionService.class);

    private final UserSessionRepository userSessionRepository;
    private final QuestionService questionService;
    private final ObjectMapper objectMapper;

    public SessionService(UserSessionRepository userSessionRepository,
                        QuestionService questionService,
                        ObjectMapper objectMapper) {
        this.userSessionRepository = userSessionRepository;
        this.questionService = questionService;
        this.objectMapper = objectMapper;
    }

    /**
     * 獲取或創建會話 ( transactional to prevent race conditions )
     */
    @Transactional
    public UserSession getOrCreateSession(String sessionId, String topicId) {
        return userSessionRepository.findBySessionIdAndTopicId(sessionId, topicId)
                .orElseGet(() -> createNewSession(sessionId, topicId));
    }

    /**
     * 創建新會話
     */
    @Transactional
    public UserSession createNewSession(String sessionId, String topicId) {
        List<Question> questions = questionService.getQuestionsByTopic(topicId);
        
        List<Long> questionIds = questions.stream()
                .map(Question::getId)
                .collect(Collectors.toList());
        Collections.shuffle(questionIds);

        UserSession session = new UserSession();
        session.setSessionId(sessionId);
        session.setTopicId(topicId);
        session.setQuestionOrder(toJson(questionIds));
        session.setCurrentIndex(0);

        log.info("Created new session for user {}, topic {} with {} questions", 
                sessionId, topicId, questionIds.size());
        
        return userSessionRepository.save(session);
    }

    /**
     * 移動到下一題
     */
    @Transactional
    public void moveToNextQuestion(String sessionId, String topicId) {
        UserSession session = getOrCreateSession(sessionId, topicId);
        
        int newIndex = (session.getCurrentIndex() == null ? 0 : session.getCurrentIndex()) + 1;
        session.setCurrentIndex(newIndex);
        userSessionRepository.save(session);
        
        log.info("Moved to next question for session {}, topic {}, index: {}", 
                sessionId, topicId, newIndex);
    }

    /**
     * 獲取下一題 ID
     */
    public Optional<Long> getNextQuestionId(String sessionId, String topicId) {
        UserSession session = getOrCreateSession(sessionId, topicId);
        
        List<Long> questionIds = parseQuestionIds(session);
        
        int index = session.getCurrentIndex() != null ? session.getCurrentIndex() : 0;
        
        if (index >= questionIds.size()) {
            return Optional.empty();
        }
        
        return Optional.of(questionIds.get(index));
    }

    private List<Long> parseQuestionIds(UserSession session) {
        try {
            return objectMapper.readValue(session.getQuestionOrder(),
                    new TypeReference<List<Long>>() {});
        } catch (Exception e) {
            log.error("Failed to parse question order", e);
            return Collections.emptyList();
        }
    }

    /**
     * 重置會話 (先刪除舊既，再創建新的)
     */
    @Transactional
    public void resetSession(String sessionId, String topicId) {
        // Delete old session first to avoid unique constraint conflict
        userSessionRepository.findBySessionIdAndTopicId(sessionId, topicId)
                .ifPresent(session -> {
                    userSessionRepository.delete(session);
                    log.info("Deleted old session for topic {} in session {}", topicId, sessionId);
                });
        
        // Create new session
        createNewSession(sessionId, topicId);
        log.info("Reset session for topic {} in session {}", topicId, sessionId);
    }

    /**
     * 刪除會話
     */
    @Transactional
    public void deleteSession(String sessionId) {
        userSessionRepository.deleteBySessionId(sessionId);
        log.info("Deleted all sessions for sessionId: {}", sessionId);
    }

    /**
     * 檢查是否最後一題
     */
    public boolean isLastQuestion(String sessionId, String topicId) {
        return userSessionRepository.findBySessionIdAndTopicId(sessionId, topicId)
                .map(session -> {
                    List<Long> questionIds = parseQuestionIds(session);
                    int currentIndex = session.getCurrentIndex() != null ? session.getCurrentIndex() : 0;
                    return currentIndex + 1 >= questionIds.size();
                })
                .orElse(true);
    }

    private String toJson(List<Long> list) {
        try {
            return objectMapper.writeValueAsString(list);
        } catch (Exception e) {
            return "[]";
        }
    }
}
