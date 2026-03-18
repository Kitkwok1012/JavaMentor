package com.javamentor.question.repository;

import com.javamentor.question.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    
    @EntityGraph(attributePaths = {"topic", "options"})
    List<Question> findByTopicTopicIdOrderByDisplayOrder(String topicId);
    
    @EntityGraph(attributePaths = {"topic", "options"})
    List<Question> findByTopicTopicId(String topicId);
    
    /**
     * Find questions by multiple topic IDs - avoids N+1 query
     */
    @EntityGraph(attributePaths = {"topic", "options"})
    List<Question> findByTopicTopicIdIn(List<String> topicIds);
    
    @Query("SELECT q.topic.topicId, COUNT(q) FROM Question q WHERE q.topic IS NOT NULL GROUP BY q.topic.topicId")
    List<Object[]> countAllGroupedByTopic();
    
    @EntityGraph(attributePaths = {"topic", "options"})
    @Query("SELECT q FROM Question q WHERE q.id NOT IN :excludeIds")
    List<Question> findExcluding(@Param("excludeIds") java.util.Set<Long> excludeIds);
    
    @EntityGraph(attributePaths = {"topic", "options"})
    @Query("SELECT q FROM Question q WHERE q.topic IS NOT NULL")
    List<Question> findAllQuestionsWithTopic();
    
    // ========== Search Methods ==========
    
    /**
     * Search questions by keyword in question text or explanation
     * Case-insensitive search - use JOIN FETCH instead of EntityGraph for proper pagination
     */
    @Query("SELECT DISTINCT q FROM Question q LEFT JOIN FETCH q.topic LEFT JOIN FETCH q.options WHERE LOWER(q.question) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(q.explanation) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Question> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    /**
     * Search questions by keyword in specific topic
     */
    @Query("SELECT DISTINCT q FROM Question q LEFT JOIN FETCH q.topic LEFT JOIN FETCH q.options WHERE q.topic.topicId = :topicId AND (LOWER(q.question) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(q.explanation) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Question> searchByKeywordAndTopic(@Param("keyword") String keyword, @Param("topicId") String topicId, Pageable pageable);

    /**
     * Search questions by keyword and difficulty
     */
    @Query("SELECT DISTINCT q FROM Question q LEFT JOIN FETCH q.topic LEFT JOIN FETCH q.options WHERE q.difficulty = :difficulty AND (LOWER(q.question) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(q.explanation) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Question> searchByKeywordAndDifficulty(@Param("keyword") String keyword, @Param("difficulty") int difficulty, Pageable pageable);

    /**
     * Search questions by keyword, topic, and difficulty
     */
    @Query("SELECT DISTINCT q FROM Question q LEFT JOIN FETCH q.topic LEFT JOIN FETCH q.options WHERE q.topic.topicId = :topicId AND q.difficulty = :difficulty AND (LOWER(q.question) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(q.explanation) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Question> searchByKeywordAndTopicAndDifficulty(@Param("keyword") String keyword, @Param("topicId") String topicId, @Param("difficulty") int difficulty, Pageable pageable);
}
