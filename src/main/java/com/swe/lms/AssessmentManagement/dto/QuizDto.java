package com.swe.lms.AssessmentManagement.dto;

import com.swe.lms.courseManagement.entity.Course;
import com.swe.lms.userManagement.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuizDto {
    private long id;
    private String title;
    private LocalDateTime startTime;
    private long courseId;
    private long instructorId;
}