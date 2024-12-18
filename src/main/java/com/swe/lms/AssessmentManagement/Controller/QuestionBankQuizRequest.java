package com.swe.lms.AssessmentManagement.Controller;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Data
public class QuestionBankQuizRequest {
    private String title;
    private int numQuestions;//34an el random function
}
