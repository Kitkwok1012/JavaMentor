package com.javamentor.repository;

import com.javamentor.entity.Question;
import com.javamentor.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    
    List<Question> findByTopicOrderByDisplayOrder(Topic topic);
    
    List<Question> findByTopicTopicIdOrderByDisplayOrder(String topicId);
    
    List<Question> findByTopicTopicId(String topicId);
    
    List<Question> findByTopicTopicIdAndDifficulty(String topicId, Integer difficulty);
    
    List<Question> findByTopicTopicIdAndDifficultyLessThanEqual(String topicId, Integer difficulty);
    
    @Query("SELECT COUNT(q) FROM Question q WHERE q.topic.topicId = :topicId")
    Long countByTopicId(String topicId);
}
