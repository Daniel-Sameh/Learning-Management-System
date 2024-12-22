package com.swe.lms.AssessmentManagement.Mapper;

import com.swe.lms.AssessmentManagement.Repository.QuizRepository;
import com.swe.lms.AssessmentManagement.dto.QuestionDto;
import com.swe.lms.AssessmentManagement.entity.Questions.Question;
import com.swe.lms.courseManagement.Repository.CourseRepository;
import com.swe.lms.courseManagement.entity.Course;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public class QuestionMapper {

    public static QuestionDto toDTO(Question question) {
        QuestionDto dto= new QuestionDto();
        dto.setCourseid(question.getCourse().getId());
        dto.setCourseName(question.getCourse().getName());
        dto.setScore(question.getScore());
        dto.setQuestionText(question.getQuestionText());
        dto.setCorrectAnswer(question.getCorrectAnswer());
        dto.setId(question.getId());
        return dto;
    }
}
