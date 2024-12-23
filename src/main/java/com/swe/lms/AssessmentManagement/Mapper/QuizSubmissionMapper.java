package com.swe.lms.AssessmentManagement.Mapper;

import com.swe.lms.AssessmentManagement.dto.QuizQuestionAnswerDto;
import com.swe.lms.AssessmentManagement.dto.QuizSubmissionDto;
import com.swe.lms.AssessmentManagement.entity.QuizSubmission;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component

public class QuizSubmissionMapper {
    public static QuizSubmissionDto toDTO(QuizSubmission quizSubmission){
        QuizSubmissionDto dto= new QuizSubmissionDto();
        dto.setQuizId(quizSubmission.getQuiz().getId());
        dto.setStudentId(quizSubmission.getStudent().getId());
        dto.setScore(quizSubmission.getScore());
        dto.setSubmissionTime(quizSubmission.getSubmissionTime());
        List<QuizQuestionAnswerDto> answerDTOs = quizSubmission.getAnswers().stream()
                .map(answer -> {
                    QuizQuestionAnswerDto answerDTO = new QuizQuestionAnswerDto();
                    answerDTO.setQuestionId(answer.getQuestion().getId());
                    answerDTO.setAnswer(answer.getAnswer());
                    answerDTO.setCorrect(answer.isCorrect());
                    return answerDTO;
                })
                .collect(Collectors.toList());

        dto.setAnswers(answerDTOs);
        return dto;
    }
}
