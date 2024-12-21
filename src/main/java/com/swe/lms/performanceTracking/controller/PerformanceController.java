package com.swe.lms.performanceTracking.controller;

import com.swe.lms.AssessmentManagement.Service.AssignmentService;
import com.swe.lms.AssessmentManagement.Service.QuizSubmissionService;
import com.swe.lms.AssessmentManagement.dto.AssignmentSubmissionDto;
import com.swe.lms.AssessmentManagement.entity.AssignmentSubmission;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/performance")
//@RequiredArgsConstructor
public class PerformanceController {
    @Autowired
    private QuizSubmissionService quizSubmissionService;

    @Autowired
    private AssignmentService assignmentService;

    @GetMapping("/assignment/{assignmentId}")
    public ResponseEntity<List<AssignmentSubmissionDto>> getAssignmentPerformance(@PathVariable Long assignmentId) {
        System.out.println("I entered the get assignment performance!!!!!!");
        List<AssignmentSubmission> assignmentSubmission = assignmentService.getSubmissions(assignmentId);
        System.out.println("We got the assignment submissions");
        List<AssignmentSubmissionDto> assignmentSubmissionDtos = assignmentSubmission.stream().map(
                submission -> new AssignmentSubmissionDto(
                        submission.getAssignment().getId() ,submission.getStudent().getId(), submission.getMedia(),
                        submission.getSubmissionTime().toString() ,submission.getGrade())).toList();
        System.out.println("We mapped the assignment submissions");
        return ResponseEntity.ok(assignmentSubmissionDtos);
    }

}
