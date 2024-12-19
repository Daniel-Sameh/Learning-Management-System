package com.swe.lms.AssessmentManagement.Service;

import com.swe.lms.AssessmentManagement.entity.Questions.MCQOption;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class QuestionRequest {
    private String questiontype;
    private String questionText;
    private String correctAnswer;
    private float score;
    private int correctOptionIndex;
    private List<MCQOption> options;
}
