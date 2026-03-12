package com.javamentor.progress.service;

import com.javamentor.progress.dto.TopicProgressDto;
import com.javamentor.progress.entity.UserProgress;
import com.javamentor.progress.repository.UserProgressRepository;
import com.javamentor.question.entity.Question;
import com.javamentor.question.entity.Topic;
import com.javamentor.question.service.QuestionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Progress Service - 負責答題進度追蹤同埋統計
 */
@Service
public class ProgressService {

    private static final Logger log = LoggerFactory.getLogger(ProgressService.class);

    private final UserProgressRepository userProgressRepository;
    private final QuestionService questionService;

    public ProgressService(UserProgressRepository userProgressRepository, QuestionService questionService) {
        this.userProgressRepository = userProgressRepository;
        this.questionService = questionService;
    }

    /**
     * 記錄答題結果
     */
    @Transactional
    public UserProgress recordAnswer(String sessionId, Long questionId, String answer, boolean isCorrect) {
        Question question = questionService.getQuestionEntityById(questionId);
        
        UserProgress progress = new UserProgress();
        progress.setSessionId(sessionId);
        progress.setQuestion(question);
        progress.setUserAnswer(answer);
        progress.setIsCorrect(isCorrect);
        
        log.info("Answer recorded for session {}, question {}: correct={}", 
                sessionId, questionId, isCorrect);
        
        return userProgressRepository.save(progress);
    }

    /**
     * 獲取錯題列表
     */
    @Transactional(readOnly = true)
    public List<UserProgress> getWrongQuestions(String sessionId) {
        return userProgressRepository.findBySessionIdAndIsCorrectFalseOrderByAnsweredAtDesc(sessionId);
    }

    /**
     * 獲取 topic 進度 (optimized - no N+1)
     */
    @Transactional(readOnly = true)
    public List<TopicProgressDto> getTopicProgress(String sessionId) {
        List<Topic> topics = questionService.getAllTopics();
        
        // Get ALL questions once, then group by topicId (no N+1)
        List<Question> allQuestions = questionService.getAllQuestions();
        
        Map<String, Long> questionCountMap = new HashMap<>();
        for (Question q : allQuestions) {
            String topicId = q.getTopic().getTopicId();
            questionCountMap.merge(topicId, 1L, Long::sum);
        }
        
        // Get user progress stats per topic
        Map<String, TopicStats> statsMap = new HashMap<>();
        List<Object[]> stats = userProgressRepository.getTopicStatsGrouped(sessionId);
        for (Object[] row : stats) {
            String topicId = (String) row[0];
            long total = ((Number) row[1]).longValue();
            long correct = ((Number) row[2]).longValue();
            statsMap.put(topicId, new TopicStats(total, correct));
        }

        List<TopicProgressDto> progressList = new ArrayList<>();
        for (Topic topic : topics) {
            String topicId = topic.getTopicId();
            Long total = questionCountMap.getOrDefault(topicId, 0L);
            
            TopicStats stats2 = statsMap.getOrDefault(topicId, new TopicStats(0, 0));
            
            TopicProgressDto dto = new TopicProgressDto();
            dto.setTopicId(topicId);
            dto.setTopicName(topic.getName());
            dto.setDescription(topic.getDescription());
            dto.setTotalQuestions(total);
            dto.setCorrectAnswers(stats2.correct);
            dto.setAnsweredQuestions(stats2.total);
            dto.setAccuracy(total > 0 ? (double) stats2.correct / total * 100 : 0);

            progressList.add(dto);
        }

        return progressList;
    }

    /**
     * 獲取 overall 統計
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getUserStats(String sessionId) {
        long totalAnswered = userProgressRepository.countBySessionId(sessionId);
        long totalCorrect = userProgressRepository.countCorrectBySessionId(sessionId);
        long totalWrong = totalAnswered - totalCorrect;
        double accuracy = totalAnswered > 0 ? (double) totalCorrect / totalAnswered * 100 : 0;

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalAnswered", totalAnswered);
        stats.put("totalCorrect", totalCorrect);
        stats.put("totalWrong", totalWrong);
        stats.put("accuracy", Math.round(accuracy * 100) / 100.0);

        log.debug("User stats for session {}: {}", sessionId, stats);
        return stats;
    }

    /**
     * 重置所有 progress
     */
    @Transactional
    public void resetAllProgress(String sessionId) {
        userProgressRepository.deleteBySessionId(sessionId);
        log.info("Reset all progress for session {}", sessionId);
    }

    private static class TopicStats {
        long total;
        long correct;
        
        TopicStats(long total, long correct) {
            this.total = total;
            this.correct = correct;
        }
    }
}
