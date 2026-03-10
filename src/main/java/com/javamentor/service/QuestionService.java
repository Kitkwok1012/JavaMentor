package com.javamentor.service;

import com.javamentor.dto.*;
import com.javamentor.entity.*;
import com.javamentor.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuestionService {
    
    private final QuestionRepository questionRepository;
    private final TopicRepository topicRepository;
    private final UserProgressRepository userProgressRepository;
    private final FollowUpRecommender followUpRecommender;
    
    private final Map<String, List<Long>> questionOrderMap = new HashMap<>();
    private final Map<String, Integer> currentIndexMap = new HashMap<>();
    
    public QuestionService(QuestionRepository questionRepository, TopicRepository topicRepository, 
                          UserProgressRepository userProgressRepository,
                          FollowUpRecommender followUpRecommender) {
        this.questionRepository = questionRepository;
        this.topicRepository = topicRepository;
        this.userProgressRepository = userProgressRepository;
        this.followUpRecommender = followUpRecommender;
    }
    
    public List<Topic> getAllTopics() {
        return topicRepository.findAll();
    }
    
    public Topic getTopicById(String topicId) {
        return topicRepository.findByTopicId(topicId).orElse(null);
    }
    
    public QuestionDto getQuestionById(Long questionId) {
        Question question = questionRepository.findById(questionId).orElse(null);
        if (question == null) return null;
        return mapToDto(question);
    }
    
    public void moveToNextQuestion(String topicId) {
        int currentIndex = currentIndexMap.getOrDefault(topicId, 0);
        currentIndexMap.put(topicId, currentIndex + 1);
    }
    
    public QuestionDto getNextQuestion(String topicId) {
        List<Long> questionIds = questionOrderMap.computeIfAbsent(topicId, k -> {
            List<Question> questions = questionRepository.findByTopicTopicIdOrderByDisplayOrder(k);
            List<Long> ids = questions.stream().map(Question::getId).collect(Collectors.toList());
            Collections.shuffle(ids);
            return ids;
        });
        
        int index = currentIndexMap.computeIfAbsent(topicId, k -> 0);
        
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
    
    @Transactional
    public AnswerResponseDto submitAnswer(Long questionId, String answer) {
        Question question = questionRepository.findById(questionId).orElse(null);
        if (question == null) {
            return null;
        }
        
        boolean isCorrect;
        if (Boolean.TRUE.equals(question.getMultiSelect())) {
            isCorrect = checkMultiSelectAnswer(answer, question.getCorrectAnswer());
        } else {
            isCorrect = answer.equalsIgnoreCase(question.getCorrectAnswer());
        }
        
        UserProgress progress = new UserProgress();
        progress.setQuestion(question);
        progress.setUserAnswer(answer);
        progress.setIsCorrect(isCorrect);
        userProgressRepository.save(progress);
        
        AnswerResponseDto response = new AnswerResponseDto();
        response.setCorrect(isCorrect);
        response.setCorrectAnswer(question.getCorrectAnswer());
        response.setExplanation(question.getExplanation());
        
        String followUp = isCorrect ? question.getFollowUpCorrect() : question.getFollowUpWrong();
        if (followUp != null && !followUp.isBlank()) {
            response.setHasFollowUp(true);
            response.setFollowUpQuestion(followUp);
            response.setFollowUpOptions(List.of("A. 理解", "B. 再諗下", "C. 唔識", "D. 其他"));
        } else {
            response.setHasFollowUp(false);
        }
        
        String topicId = question.getTopic().getTopicId();
        int currentIndex = currentIndexMap.getOrDefault(topicId, 0);
        response.setIsLastQuestion(currentIndex + 1 >= questionOrderMap.get(topicId).size());
        
        return response;
    }
    
    public List<UserProgress> getWrongQuestions() {
        return userProgressRepository.findByIsCorrectFalseOrderByAnsweredAtDesc();
    }
    
    public List<TopicProgressDto> getTopicProgress() {
        List<Topic> topics = topicRepository.findAll();
        List<TopicProgressDto> progressList = new ArrayList<>();
        
        for (Topic topic : topics) {
            String topicId = topic.getTopicId();
            Long total = questionRepository.countByTopicId(topicId);
            Long correct = userProgressRepository.countCorrectByTopicId(topicId);
            
            TopicProgressDto dto = new TopicProgressDto();
            dto.setTopicId(topicId);
            dto.setTopicName(topic.getName());
            dto.setDescription(topic.getDescription());
            dto.setTotalQuestions(total);
            dto.setCorrectAnswers(correct);
            dto.setAnsweredQuestions(userProgressRepository.countByTopicId(topicId));
            dto.setAccuracy(total > 0 ? (double) correct / total * 100 : 0);
            
            progressList.add(dto);
        }
        
        return progressList;
    }
    
    public void resetTopic(String topicId) {
        currentIndexMap.put(topicId, 0);
        questionOrderMap.remove(topicId);
    }
    
    public List<QuestionDto> findRelatedQuestions(Long currentQuestionId, boolean answeredCorrect) {
        Question current = questionRepository.findById(currentQuestionId).orElse(null);
        if (current == null || current.getTags() == null) {
            return Collections.emptyList();
        }
        
        String topicId = current.getTopic().getTopicId();
        List<Question> candidates = questionRepository.findByTopicTopicIdOrderByDisplayOrder(topicId);
        Set<String> currentTags = new HashSet<>(Arrays.asList(current.getTags().split(",")));
        int currentDifficulty = current.getDifficulty() != null ? current.getDifficulty() : 1;
        
        List<Question> related = candidates.stream()
            .filter(q -> !q.getId().equals(currentQuestionId))
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
    
    public QuestionDto recommendNextQuestion(Long currentQuestionId, boolean correct) {
        Question recommended = followUpRecommender.recommend(currentQuestionId, correct);
        if (recommended == null) return null;
        return mapToDto(recommended);
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
