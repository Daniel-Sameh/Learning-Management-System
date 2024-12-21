package com.swe.lms.AssessmentManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class AssignmentDto {
    private String title;
    private String description;
    private String deadline;
    private Long courseId;
    private Long instructorId;
}
