package com.javamentor.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javamentor.config.AppConstants;
import com.javamentor.config.CaffeineConfig;
import com.javamentor.exception.*;
import com.javamentor.mocktest.dto.AnswerResponseDto;
import com.javamentor.progress.dto.TopicProgressDto;
import com.javamentor.progress.entity.UserProgress;
import com.javamentor.progress.repository.UserProgressRepository;
import com.javamentor.question.dto.QuestionDto;
import com.javamentor.question.entity.Question;
import com.javamentor.question.entity.Topic;
import com.javamentor.question.repository.QuestionRepository;
import com.javamentor.question.repository.TopicRepository;
import com.javamentor.session.entity.UserSession;
import com.javamentor.session.repository.UserSessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuestionService {

    private static final Logger log = LoggerFactory.getLogger(QuestionService.class);

    private final QuestionRepository questionRepository;
    private final TopicRepository topicRepository;
    private final UserProgressRepository userProgressRepository;
    private final UserSessionRepository userSessionRepository;
    private final FollowUpRecommender followUpRecommender;
    private final ObjectMapper objectMapper;

    public QuestionService(QuestionRepository questionRepository,
                          TopicRepository topicRepository,
                          UserProgressRepository userProgressRepository,
                          UserSessionRepository userSessionRepository,
                          FollowUpRecommender followUpRecommender,
                          ObjectMapper objectMapper) {
        this.questionRepository = questionRepository;
        this.topicRepository = topicRepository;
        this.userProgressRepository = userProgressRepository;
        this.userSessionRepository = userSessionRepository;
        this.followUpRecommender = followUpRecommender;
        this.objectMapper = objectMapper;
    }

    @Cacheable("topics")
    @Transactional(readOnly = true)
    public List<Topic> getAllTopics() {
        log.debug("Loading all topics from database");
        return topicRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Topic getTopicById(String topicId) {
        return topicRepository.findByTopicId(topicId)
                .orElseThrow(() -> new TopicNotFoundException(topicId));
    }

    @Transactional(readOnly = true)
    public QuestionDto getQuestionById(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException(questionId));
        return mapToDto(question);
    }

    @Transactional
    public void moveToNextQuestion(String sessionId, String topicId) {
        UserSession session = userSessionRepository.findBySessionIdAndTopicId(sessionId, topicId)
                .orElseGet(() -> createNewSession(sessionId, topicId));

        int newIndex = (session.getCurrentIndex() == null ? 0 : session.getCurrentIndex()) + 1;
        session.setCurrentIndex(newIndex);
        userSessionRepository.save(session);
        log.info("Moved to next question for session {}, topic {}, index: {}", sessionId, topicId, newIndex);
    }

    public QuestionDto getNextQuestion(String sessionId, String topicId) {
        UserSession session = userSessionRepository.findBySessionIdAndTopicId(sessionId, topicId)
                .orElseGet(() -> createNewSession(sessionId, topicId));

        List<Long> questionIds;
        try {
            questionIds = objectMapper.readValue(session.getQuestionOrder(),
                    new TypeReference<List<Long>>() {});
        } catch (Exception e) {
            log.error("Failed to parse question order", e);
            questionIds = generateNewQuestionOrder(topicId);
            session.setQuestionOrder(toJson(questionIds));
        }

        int index = session.getCurrentIndex() != null ? session.getCurrentIndex() : 0;

        if (index >= questionIds.size()) {
            return null;
        }

        Long questionId = questionIds.get(index);
        Question question = questionRepository.findById(questionId).orElse(null);

        if (question == null) {
            return null;
        }

        return mapToDto(question);
    }

    private UserSession createNewSession(String sessionId, String topicId) {
        List<Long> questionIds = generateNewQuestionOrder(topicId);

        UserSession session = new UserSession();
        session.setSessionId(sessionId);
        session.setTopicId(topicId);
        session.setQuestionOrder(toJson(questionIds));
        session.setCurrentIndex(0);

        log.info("Created new session for user {}, topic {} with {} questions", sessionId, topicId, questionIds.size());
        return userSessionRepository.save(session);
    }

    private List<Long> generateNewQuestionOrder(String topicId) {
        List<Question> questions = questionRepository.findByTopicTopicIdOrderByDisplayOrder(topicId);
        List<Long> ids = questions.stream()
                .map(Question::getId)
                .collect(Collectors.toList());
        Collections.shuffle(ids);
        return ids;
    }

    private String toJson(List<Long> list) {
        try {
            return objectMapper.writeValueAsString(list);
        } catch (Exception e) {
            return "[]";
        }
    }

    @Transactional
    public AnswerResponseDto submitAnswer(String sessionId, Long questionId, String answer) {
        if (questionId == null) {
            throw new InvalidAnswerException("Question ID 不能為空");
        }
        if (answer == null || answer.trim().isEmpty()) {
            throw new InvalidAnswerException("答案不能為空");
        }
        answer = answer.trim().toUpperCase();

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException(questionId));

        boolean isCorrect;
        if (Boolean.TRUE.equals(question.getMultiSelect())) {
            isCorrect = checkMultiSelectAnswer(answer, question.getCorrectAnswer());
        } else {
            isCorrect = answer.equalsIgnoreCase(question.getCorrectAnswer());
        }

        UserProgress progress = new UserProgress();
        progress.setSessionId(sessionId);
        progress.setQuestion(question);
        progress.setUserAnswer(answer);
        progress.setIsCorrect(isCorrect);
        userProgressRepository.save(progress);

        log.info("Answer submitted for session {}, question {}: correct={}", sessionId, questionId, isCorrect);

        AnswerResponseDto response = new AnswerResponseDto();
        response.setCorrect(isCorrect);
        response.setCorrectAnswer(question.getCorrectAnswer());
        response.setExplanation(question.getExplanation());

        String followUp = isCorrect ? question.getFollowUpCorrect() : question.getFollowUpWrong();
        if (followUp != null && !followUp.isBlank()) {
            response.setHasFollowUp(true);
            response.setFollowUpQuestion(followUp);
            response.setFollowUpOptions(Arrays.asList("A", "B", "C"));
        } else {
            response.setHasFollowUp(false);
        }

        String topicId = question.getTopic().getTopicId();
        UserSession session = userSessionRepository.findBySessionIdAndTopicId(sessionId, topicId)
                .orElse(null);
        if (session != null) {
            List<Long> questionIds;
            try {
                questionIds = objectMapper.readValue(session.getQuestionOrder(),
                        new TypeReference<List<Long>>() {});
            } catch (Exception e) {
                questionIds = Collections.emptyList();
            }
            int currentIndex = session.getCurrentIndex() != null ? session.getCurrentIndex() : 0;
            response.setIsLastQuestion(currentIndex + 1 >= questionIds.size());
        } else {
            response.setIsLastQuestion(true);
        }

        return response;
    }

    @Transactional(readOnly = true)
    public List<UserProgress> getWrongQuestions(String sessionId) {
        return userProgressRepository.findBySessionIdAndIsCorrectFalseOrderByAnsweredAtDesc(sessionId);
    }

    /**
     * Get topic progress - optimized with single query to fix N+1 problem
     */
    @Transactional(readOnly = true)
    public List<TopicProgressDto> getTopicProgress(String sessionId) {
        List<Topic> topics = topicRepository.findAll();
        
        // Single query to get all question counts grouped by topic
        Map<String, Long> questionCountMap = new HashMap<>();
        List<Object[]> questionCounts = questionRepository.countAllGroupedByTopic();
        for (Object[] row : questionCounts) {
            String topicId = (String) row[0];
            long count = ((Number) row[1]).longValue();
            questionCountMap.put(topicId, count);
        }
        
        // Single query to get all user progress stats grouped by topic
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

    private static class TopicStats {
        long total;
        long correct;
        TopicStats(long total, long correct) {
            this.total = total;
            this.correct = correct;
        }
    }

    @Transactional
    public void resetTopic(String sessionId, String topicId) {
        userSessionRepository.findBySessionIdAndTopicId(sessionId, topicId)
                .ifPresent(session -> {
                    List<Long> newOrder = generateNewQuestionOrder(topicId);
                    session.setQuestionOrder(toJson(newOrder));
                    session.setCurrentIndex(0);
                    userSessionRepository.save(session);
                    log.info("Reset topic {} for session {}", topicId, sessionId);
                });
    }

    @Transactional
    public void resetAllProgress(String sessionId) {
        userSessionRepository.deleteBySessionId(sessionId);
        userProgressRepository.deleteBySessionId(sessionId);
        log.info("Reset all progress for session {}", sessionId);
    }

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

    @Transactional(readOnly = true)
    public List<QuestionDto> findRelatedQuestions(Long currentQuestionID, boolean answeredCorrect) {
        Question current = questionRepository.findById(currentQuestionID).orElse(null);
        if (current == null || current.getTags() == null) {
            return Collections.emptyList();
        }

        String topicId = current.getTopic().getTopicId();
        List<Question> candidates = questionRepository.findByTopicTopicIdOrderByDisplayOrder(topicId);
        Set<String> currentTags = new HashSet<>(Arrays.asList(current.getTags().split(",")));
        int currentDifficulty = current.getDifficulty() != null ? current.getDifficulty() : 1;

        List<Question> related = candidates.stream()
                .filter(q -> !q.getId().equals(currentQuestionID))
                .filter(q -> q.getTags() != null && hasSharedTags(currentTags, q.getTags()))
                .filter(q -> {
                    int qDifficulty = q.getDifficulty() != null ? q.getDifficulty() : 1;
                    if (answeredCorrect) {
                        return qDifficulty >= currentDifficulty;
                    } else {
                        return qDifficulty <= currentDifficulty;
                    }
                })
                .sorted(Comparator.comparingInt((Question q) -> q.getDifficulty() != null ? q.getDifficulty() : 1))
                .limit(2)
                .collect(Collectors.toList());

        return related.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    private boolean hasSharedTags(Set<String> tags1, String tags2) {
        if (tags2 == null) return false;
        Set<String> set2 = new HashSet<>(Arrays.asList(tags2.split(",")));
        return !Collections.disjoint(tags1, set2);
    }

    private boolean checkMultiSelectAnswer(String userAnswer, String correctAnswer) {
        if (userAnswer == null || correctAnswer == null) return false;
        String user = normalizeAnswer(userAnswer);
        String correct = normalizeAnswer(correctAnswer);
        return user.equals(correct);
    }

    private String normalizeAnswer(String answer) {
        String cleaned = answer.toUpperCase().replaceAll("\\s+", "");
        String[] parts;
        if (cleaned.contains(",")) {
            parts = cleaned.split(",");
        } else {
            parts = cleaned.split("");
        }
        Arrays.sort(parts);
        return String.join(",", parts);
    }

    @Transactional(readOnly = true)
    public QuestionDto recommendNextQuestion(String sessionId, Long currentQuestionId, boolean correct) {
        Question recommended = followUpRecommender.recommend(sessionId, currentQuestionId, correct);
        if (recommended == null) return null;
        return mapToDto(recommended);
    }

    // ========== Search Methods ==========

    /**
     * Search questions by keyword
     */
    @Transactional(readOnly = true)
    public List<QuestionDto> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Collections.emptyList();
        }
        
        log.info("Searching questions with keyword: {}", keyword);
        List<Question> results = questionRepository.searchByKeyword(keyword.trim());
        return results.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    /**
     * Search questions by keyword in specific topic
     */
    @Transactional(readOnly = true)
    public List<QuestionDto> searchByTopic(String keyword, String topicId) {
        if (keyword == null || keyword.trim().isEmpty() || topicId == null) {
            return Collections.emptyList();
        }
        
        log.info("Searching questions with keyword: {} in topic: {}", keyword, topicId);
        List<Question> results = questionRepository.searchByKeywordAndTopic(keyword.trim(), topicId);
        return results.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    private QuestionDto mapToDto(Question question) {
        QuestionDto dto = new QuestionDto();
        dto.setId(question.getId());
        dto.setTopicId(question.getTopic().getTopicId());
        dto.setTopicName(question.getTopic().getName());
        dto.setQuestion(question.getQuestion());
        dto.setOptionA(question.getOptionA());
        dto.setOptionB(question.getOptionB());
        dto.setOptionC(question.getOptionC());
        dto.setOptionD(question.getOptionD());
        dto.setOptionE(question.getOptionE());
        dto.setMultiSelect(question.getMultiSelect());
        dto.setDifficulty(question.getDifficulty());
        return dto;
    }
}
