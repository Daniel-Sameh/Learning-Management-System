package com.swe.lms.AssessmentManagement.Controller;


import lombok.*;

@Setter
@Getter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizUpdateRequest {

    private String title;
    // private Integer questionsNum;
    private String startTime;
    private Integer timeLimit;
    private long courseId;
}
