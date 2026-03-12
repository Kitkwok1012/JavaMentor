package com.javamentor.question.repository;

import com.javamentor.question.entity.Question;
import com.javamentor.question.entity.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    
    List<Question> findByTopicTopicIdOrderByDisplayOrder(String topicId);
    
    List<Question> findByTopicTopicId(String topicId);
    
    List<Question> findByTopicTopicIdAndDifficulty(String topicId, Integer difficulty);
    
    List<Question> findByTopicTopicIdAndDifficultyLessThanEqual(String topicId, Integer difficulty);
    
    @Query("SELECT COUNT(q) FROM Question q WHERE q.topic.topicId = :topicId")
    Long countByTopicId(@Param("topicId") String topicId);
    
    @Query("SELECT q.topic.topicId, COUNT(q) FROM Question q GROUP BY q.topic.topicId")
    List<Object[]> countAllGroupedByTopic();
    
    @Query("SELECT q FROM Question q WHERE q.id NOT IN :excludeIds")
    List<Question> findExcluding(@Param("excludeIds") java.util.Set<Long> excludeIds);
    
    List<Question> findByTopicTopicIdNotNull();
    
    // ========== Search Methods ==========
    
    /**
     * Search questions by keyword in question text or explanation
     * Case-insensitive search
     */
    @Query("SELECT q FROM Question q WHERE LOWER(q.question) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(q.explanation) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Question> searchByKeyword(@Param("keyword") String keyword);
    
    /**
     * Search questions by keyword with pagination
     */
    @Query("SELECT q FROM Question q WHERE LOWER(q.question) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(q.explanation) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Question> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    /**
     * Search questions by keyword in specific topic
     */
    @Query("SELECT q FROM Question q WHERE q.topic.topicId = :topicId AND (LOWER(q.question) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(q.explanation) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Question> searchByKeywordAndTopic(@Param("keyword") String keyword, @Param("topicId") String topicId);
    
    /**
     * Search questions by keyword in specific difficulty range
     */
    @Query("SELECT q FROM Question q WHERE (LOWER(q.question) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(q.explanation) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND q.difficulty <= :maxDifficulty")
    List<Question> searchByKeywordWithDifficulty(@Param("keyword") String keyword, @Param("maxDifficulty") Integer maxDifficulty);
}
