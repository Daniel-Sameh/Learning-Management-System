package com.swe.lms.AssessmentManagement.dto;

import com.swe.lms.AssessmentManagement.entity.Quiz;
import com.swe.lms.courseManagement.entity.Course;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuestionDto {
    private long id;
    private String questionText;
    private float score;
    private long courseid;
    private String CourseName;
    private String correctAnswer;
}
