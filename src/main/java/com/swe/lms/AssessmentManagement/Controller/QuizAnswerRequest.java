package com.swe.lms.AssessmentManagement.Controller;

import lombok.Data;

@Data
public class QuizAnswerRequest {
    private Long questionId;
    private String answer;
}
