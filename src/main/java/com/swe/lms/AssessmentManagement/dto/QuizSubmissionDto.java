package com.swe.lms.AssessmentManagement.dto;

import com.swe.lms.AssessmentManagement.entity.Quiz;
import com.swe.lms.AssessmentManagement.entity.QuizQuestionAnswers;
import com.swe.lms.userManagement.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuizSubmissionDto {
    private long studentId;
    private long quizId;
    private LocalDate submissionTime;
    private float score;
    private List<QuizQuestionAnswerDto> answers;
}
