package com.javamentor.question.dto;

import com.javamentor.question.entity.Question;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between Question entity and QuestionDto
 */
@Component
public class QuestionMapper {
    
    public QuestionDto toDto(Question entity) {
        if (entity == null) {
            return null;
        }
        
        QuestionDto dto = new QuestionDto();
        dto.setId(entity.getId());
        dto.setQuestion(entity.getQuestion());
        dto.setOptionA(entity.getOptionA());
        dto.setOptionB(entity.getOptionB());
        dto.setOptionC(entity.getOptionC());
        dto.setOptionD(entity.getOptionD());
        dto.setOptionE(entity.getOptionE());
        dto.setMultiSelect(entity.getMultiSelect());
        dto.setDifficulty(entity.getDifficulty());
        
        if (entity.getTopic() != null) {
            dto.setTopicId(entity.getTopic().getTopicId());
            dto.setTopicName(entity.getTopic().getName());
        }
        
        return dto;
    }
}
