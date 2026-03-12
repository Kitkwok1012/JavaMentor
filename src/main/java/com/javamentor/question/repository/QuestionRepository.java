package com.javamentor.question.repository;

import com.javamentor.question.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    
    List<Question> findByTopicTopicIdOrderByDisplayOrder(String topicId);
    
    List<Question> findByTopicTopicId(String topicId);
    
    /**
     * Find questions by multiple topic IDs - avoids N+1 query
     */
    List<Question> findByTopicTopicIdIn(List<String> topicIds);
    
    @Query("SELECT q.topic.topicId, COUNT(q) FROM Question q GROUP BY q.topic.topicId")
    List<Object[]> countAllGroupedByTopic();
    
    @Query("SELECT q FROM Question q WHERE q.id NOT IN :excludeIds")
    List<Question> findExcluding(@Param("excludeIds") java.util.Set<Long> excludeIds);
    
    @Query("SELECT q FROM Question q WHERE q.topic IS NOT NULL")
    List<Question> findAllQuestionsWithTopic();
    
    // ========== Search Methods ==========
    
    /**
     * Search questions by keyword in question text or explanation
     * Case-insensitive search
     */
    @Query("SELECT q FROM Question q WHERE LOWER(q.question) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(q.explanation) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Question> searchByKeyword(@Param("keyword") String keyword);
    
    /**
     * Search questions by keyword in specific topic
     */
    @Query("SELECT q FROM Question q WHERE q.topic.topicId = :topicId AND (LOWER(q.question) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(q.explanation) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Question> searchByKeywordAndTopic(@Param("keyword") String keyword, @Param("topicId") String topicId);
}
