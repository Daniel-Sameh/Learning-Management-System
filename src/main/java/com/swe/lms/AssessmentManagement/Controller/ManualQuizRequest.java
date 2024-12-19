package com.swe.lms.AssessmentManagement.Controller;

import com.swe.lms.AssessmentManagement.Service.QuestionRequest;
import com.swe.lms.AssessmentManagement.entity.Questions.Question;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Setter
@Getter
@Data
public class ManualQuizRequest {
    private String title;
    private List<QuestionRequest> questions;
    private Integer timelimit;
    private Integer questionsNum;

}
