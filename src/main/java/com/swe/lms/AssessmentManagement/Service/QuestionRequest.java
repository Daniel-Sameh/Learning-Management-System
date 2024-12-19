package com.swe.lms.AssessmentManagement.Service;

import com.swe.lms.AssessmentManagement.entity.Questions.MCQOption;
import com.swe.lms.courseManagement.entity.Course;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class QuestionRequest {
    private String questiontype;
    private Course course;
    private String questionText;
    private String correctAnswer;
    private float score;
    private int correctOptionIndex;
    private List<MCQOption> options;

}