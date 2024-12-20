package com.swe.lms.AssessmentManagement.Controller;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
@Data
@Setter
@Getter

public class QuestionBankQuizRequest {
    private String title;
    private Integer numQuestions;
    private Integer timeLimit;
    private long courseId;

}
