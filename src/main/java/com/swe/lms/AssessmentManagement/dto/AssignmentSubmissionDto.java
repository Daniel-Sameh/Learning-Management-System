package com.swe.lms.AssessmentManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AssignmentSubmissionDto {
    private Long assignmentId;
    private Long studentId;
    private String media;
    private String submissionTime;
    private float grade;
    private String status;
    public AssignmentSubmissionDto(Long assignmentId, Long studentId, String media,
                                   String submissionTime, float grade) {
        this.assignmentId = assignmentId;
        this.studentId = studentId;
        this.media = media;
        this.submissionTime = submissionTime;
        this.grade = grade;
    }
}
