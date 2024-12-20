package com.swe.lms.AssessmentManagement.Controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class QuizAnswerRequest {
    private Long questionId;
    private String answer;
}
