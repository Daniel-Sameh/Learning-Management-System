package com.swe.lms.AssessmentManagement.Mapper;

import com.swe.lms.AssessmentManagement.dto.QuizDto;
import com.swe.lms.AssessmentManagement.entity.Quiz;
import org.springframework.stereotype.Component;

@Component
public class QuizMapper {
    public static QuizDto toDTO(Quiz quiz){
        QuizDto dto=new QuizDto();
        dto.setCourseId(quiz.getCourse().getId());
        dto.setInstructorId(quiz.getInstructor().getId());
        dto.setStartTime(quiz.getStartTime());
        dto.setTitle(quiz.getTitle());
        dto.setId(quiz.getId());
        return dto;
    }
}
