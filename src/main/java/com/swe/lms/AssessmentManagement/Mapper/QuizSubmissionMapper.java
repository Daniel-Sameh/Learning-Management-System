package com.swe.lms.AssessmentManagement.Mapper;

import com.swe.lms.AssessmentManagement.dto.QuizSubmissionDto;
import com.swe.lms.AssessmentManagement.entity.QuizSubmission;
import org.springframework.stereotype.Component;

@Component

public class QuizSubmissionMapper {
    public static QuizSubmissionDto toDTO(QuizSubmission quizSubmission){
        QuizSubmissionDto dto= new QuizSubmissionDto();
        dto.setQuizId(quizSubmission.getQuiz().getId());
        dto.setStudentId(quizSubmission.getStudent().getId());
        dto.setScore(quizSubmission.getScore());
        dto.setSubmissionTime(quizSubmission.getSubmissionTime());
        return dto;
    }
}
