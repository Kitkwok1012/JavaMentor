package com.javamentor.question.repository;

import com.javamentor.question.entity.Question;
import com.javamentor.question.entity.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    
    @Query("SELECT q.topic.topicId, COUNT(q) FROM Question q GROUP BY q.topic.topicId")
    List<Object[]> countAllGroupedByTopic();
    
    @Query("SELECT q FROM Question q WHERE q.id NOT IN :excludeIds")
    List<Question> findExcluding(java.util.Set<Long> excludeIds);
    
    @Query("SELECT q FROM Question q")
    List<Question> findAllQuestions();
    
    List<Question> findByTopicTopicIdNotNull();
    
    // ========== Search Methods ==========
    
    /**
     * Search questions by keyword in question text or explanation
     * Case-insensitive search
     */
    @Query("SELECT q FROM Question q WHERE LOWER(q.question) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(q.explanation) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Question> searchByKeyword(String keyword);
    
    /**
     * Search questions by keyword with pagination
     */
    @Query("SELECT q FROM Question q WHERE LOWER(q.question) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(q.explanation) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Question> searchByKeyword(String keyword, Pageable pageable);
    
    /**
     * Search questions by keyword in specific topic
     */
    @Query("SELECT q FROM Question q WHERE q.topic.topicId = :topicId AND (LOWER(q.question) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(q.explanation) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Question> searchByKeywordAndTopic(String keyword, String topicId);
    
    /**
     * Search questions by keyword in specific difficulty range
     */
    @Query("SELECT q FROM Question q WHERE (LOWER(q.question) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(q.explanation) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND q.difficulty <= :maxDifficulty")
    List<Question> searchByKeywordWithDifficulty(String keyword, Integer maxDifficulty);
}
