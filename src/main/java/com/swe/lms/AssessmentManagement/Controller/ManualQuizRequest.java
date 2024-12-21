package com.swe.lms.AssessmentManagement.Controller;

import com.swe.lms.AssessmentManagement.Service.QuestionRequest;
import com.swe.lms.AssessmentManagement.entity.Questions.Question;
import com.swe.lms.courseManagement.entity.Course;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
@Setter
@Getter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ManualQuizRequest {
    private String title;
    private List<QuestionRequest> questions;
    private Integer timeLimit;
    private Integer questionsNum;
    private long courseid;
    private String startTime;


}
