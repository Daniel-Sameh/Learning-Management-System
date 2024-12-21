package com.swe.lms.AssessmentManagement.Controller;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Setter
@Getter

public class QuestionBankQuizRequest {
    private String title;
    private Integer numQuestions;
    private Integer timeLimit;
    private long courseId;
    private LocalDateTime startTime;


}
