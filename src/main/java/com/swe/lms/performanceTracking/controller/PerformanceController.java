package com.swe.lms.performanceTracking.controller;

import com.swe.lms.AssessmentManagement.Service.AssignmentService;
import com.swe.lms.AssessmentManagement.Service.QuizSubmissionService;
import com.swe.lms.AssessmentManagement.dto.AssignmentSubmissionDto;
import com.swe.lms.AssessmentManagement.entity.AssignmentSubmission;
import com.swe.lms.AssessmentManagement.entity.QuizSubmission;
import com.swe.lms.courseManagement.Service.LectureService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/performance")
//@RequiredArgsConstructor
public class PerformanceController {
    @Autowired
    private QuizSubmissionService quizSubmissionService;

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private LectureService lectureService;

    @GetMapping("/assignment/{assignmentId}")
    @PreAuthorize("hasAnyRole('ROLE_INSTRUCTOR', 'ROLE_ADMIN')")
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

    @GetMapping("/quiz/{quizId}")
    @PreAuthorize("hasAnyRole('ROLE_INSTRUCTOR', 'ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> getQuizPerformance(@PathVariable Long quizId) {
        Map<String, Object> quizSubmissions = quizSubmissionService.getQuizSubmissions(quizId);
        return ResponseEntity.ok(quizSubmissions);
    }

    @GetMapping("/attendance/{lectureId}")
    @PreAuthorize("hasAnyRole('ROLE_INSTRUCTOR', 'ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> getAttendance(@PathVariable Long lectureId) {
        return ResponseEntity.ok(lectureService.getLectureAttendanceStats(lectureId));
    }

}
