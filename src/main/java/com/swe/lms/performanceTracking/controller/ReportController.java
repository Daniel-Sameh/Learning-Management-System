package com.swe.lms.performanceTracking.controller;

import com.swe.lms.AssessmentManagement.Service.AssignmentService;
import com.swe.lms.AssessmentManagement.entity.AssignmentSubmission;
import com.swe.lms.courseManagement.Service.CourseService;
import com.swe.lms.courseManagement.Service.LectureService;
import com.swe.lms.courseManagement.dto.StudentDTO;
import com.swe.lms.courseManagement.entity.Lecture;
import com.swe.lms.performanceTracking.service.ReportService;
import com.swe.lms.userManagement.Service.AuthenticationService;
import com.swe.lms.userManagement.Service.UserService;
import com.swe.lms.userManagement.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;
    private final AssignmentService assignmentService;
    private final LectureService lectureService;
    private final AuthenticationService userService;
    private final CourseService courseService;

    @GetMapping("/course/{courseId}/excel")
    @PreAuthorize("hasAnyRole('ROLE_INSTRUCTOR', 'ROLE_ADMIN')")
    public ResponseEntity<byte[]> generateExcelReport(@PathVariable Long courseId) {
        try {
            List<List<AssignmentSubmission>> assignments = assignmentService.getSubmissionsByCourseId(courseId);
            List<Lecture> lectures = lectureService.getLecturesByCourseId(courseId);
            List<StudentDTO> students = courseService.getStudentsEnrolledInCourse(courseId);
            byte[] report = reportService.generatePerformanceReport(assignments, lectures, students);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "performance_report.xlsx");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(report);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/course/{courseId}/statistics")
    @PreAuthorize("hasAnyRole('ROLE_INSTRUCTOR', 'ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> getPerformanceStatistics(@PathVariable Long courseId) {
        try {
            List<List<AssignmentSubmission>> assignments = assignmentService.getSubmissionsByCourseId(courseId);
            List<Lecture> lectures = lectureService.getLecturesByCourseId(courseId);
            List<User> students = courseService.getStudentsByCourseId(courseId);
            Map<String, Object> stats = reportService.generatePerformanceStats(assignments, lectures, students);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/course/{courseId}/charts")
    @PreAuthorize("hasAnyRole('ROLE_INSTRUCTOR', 'ROLE_ADMIN')")
    public ResponseEntity<byte[]> getPerformanceCharts(@PathVariable Long courseId) {
        try {
            // Get statistics first
            List<List<AssignmentSubmission>> assignments = assignmentService.getSubmissionsByCourseId(courseId);
            List<Lecture> lectures = lectureService.getLecturesByCourseId(courseId);
            List<Map<String,Object>> attendanceStats = new ArrayList<>();
            List<User> students = courseService.getStudentsByCourseId(courseId);
            Map<String, Object> stats = reportService.generatePerformanceStats(assignments, lectures, students);
            if (stats == null) {
                return ResponseEntity.notFound().build();
            }
            System.out.println("1- I hate my life...");
            // Generate charts
            byte[] chartImage = reportService.generateCharts(stats);
            System.out.println("2- I hate my life...");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentDispositionFormData("attachment", "performance_charts.png");
            System.out.println("3- I hate my life...");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(chartImage);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}