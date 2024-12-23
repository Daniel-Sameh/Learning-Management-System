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
        List<QuestionDto> questionDTOs = quiz.getQuestions().stream()
                .map(question -> {
                    QuestionDto questionDTO = new QuestionDto();
                    questionDTO.setId(question.getId());
                    questionDTO.setQuestionText(question.getQuestionText());
                    questionDTO.setScore(question.getScore());
                    questionDTO.setCourseid(question.getCourse().getId());
                    questionDTO.setCourseName(question.getCourse().getName());
                    return questionDTO;
                })
                .collect(Collectors.toList());
        dto.setQuestions(questionDTOs);
        return dto;
    }
}
