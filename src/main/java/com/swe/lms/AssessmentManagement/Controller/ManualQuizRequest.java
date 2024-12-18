package com.swe.lms.AssessmentManagement.Controller;

import com.swe.lms.AssessmentManagement.entity.Questions.IQuestion;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Setter
@Getter
public class ManualQuizRequest {
    private String title;
    private List<IQuestion> questions;
}
