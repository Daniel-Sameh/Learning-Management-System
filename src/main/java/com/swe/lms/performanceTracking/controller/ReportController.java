package com.swe.lms.performanceTracking.controller;

import com.swe.lms.AssessmentManagement.Service.AssignmentService;
import com.swe.lms.AssessmentManagement.Service.QuizService;
import com.swe.lms.AssessmentManagement.Service.QuizSubmissionService;
import com.swe.lms.AssessmentManagement.dto.QuizDto;
import com.swe.lms.AssessmentManagement.entity.AssignmentSubmission;
import com.swe.lms.AssessmentManagement.entity.Quiz;
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
import java.util.HashMap;
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
    private final QuizSubmissionService quizSubmissionService;
    private final QuizService quizService;
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
    @PreAuthorize("hasAnyRole('ROLE_INSTRUCTOR', 'ROLE_ADMIN')") //there is a problem here
    public ResponseEntity<Map<String, Object>> getPerformanceStatistics(@PathVariable Long courseId) {
        try {
            List<Map<String, Object>> courseStats = getQuizStats(courseId);
            Map<String, Object> stats = reportService.generatePerformanceStats(courseStats);
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
            List<Map<String, Object>> courseStats = getQuizStats(courseId);
            Map<String, Object> stats = reportService.generatePerformanceStats(courseStats);
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
    private List<Map<String, Object>> getQuizStats(Long courseId) {
        List<Map<String, Object>> courseStats = new ArrayList<>();
        System.out.println("We are getting stats...");

        List<List<AssignmentSubmission>> assignments = assignmentService.getSubmissionsByCourseId(courseId);
        courseStats.add(Map.of("assignments", assignments)); // 0

        List<Lecture> lectures = lectureService.getLecturesByCourseId(courseId);
        courseStats.add(Map.of("lectures", lectures)); //1

        List<User> students = courseService.getStudentsByCourseId(courseId);
        courseStats.add(Map.of("students", students));//2

        List<QuizDto> quizzes = quizService.getQuizzesByCourseId(courseId);
        courseStats.add(Map.of("quizzes", quizzes));//3
//        long averageScoreOfAllQuizzes = 0;
//        long totalQuizzes = quizzes.size();

        List<Map<String,Object>> quizStats = new ArrayList<>();
        for (QuizDto quiz : quizzes) {
            Map<String, Object> quizSubmissions= new HashMap<>();
            quizSubmissions.put("submissions", quizSubmissionService.getQuizSubmissions(quiz.getId()));
//            Map<String, Object> quizSubmissions = quizSubmissions = quizSubmissionService.getQuizSubmissions(quiz.getId());
            quizStats.add(quizSubmissions);
//            averageScoreOfAllQuizzes = averageScoreOfAllQuizzes+ (long)quizSubmissions.getLast().get("average");
        }
        System.out.println("We are done getting stats...");
//        averageScoreOfAllQuizzes = averageScoreOfAllQuizzes/totalQuizzes;
//        courseStats.add(Map.of("averageScoreOfAllQuizzes", averageScoreOfAllQuizzes));
        courseStats.add(Map.of("quizStats", quizStats)); //4
        System.out.println("We are returning stats...");
        return courseStats;

    }
}