package com.swe.lms.AssessmentManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuizQuestionAnswerDto {
    private long questionId;
    private  String answer;
    private boolean correct;
}
