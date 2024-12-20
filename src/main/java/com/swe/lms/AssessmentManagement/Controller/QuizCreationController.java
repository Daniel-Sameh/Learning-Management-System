package com.swe.lms.AssessmentManagement.Controller;
import com.swe.lms.AssessmentManagement.Service.QuizService;
import com.swe.lms.AssessmentManagement.entity.Quiz;
import com.swe.lms.courseManagement.Service.CourseService;
import com.swe.lms.courseManagement.entity.Course;
import com.swe.lms.userManagement.entity.Role;
import com.swe.lms.userManagement.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/quizzes")
@AllArgsConstructor

public class QuizCreationController {
    private final QuizService quizService;
    private final CourseService courseService;




    @PostMapping("/create/bank")
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR')")
    public ResponseEntity<?> createQuizFromBank(@AuthenticationPrincipal User instructor, @RequestBody QuestionBankQuizRequest quizRequest) {
        if (instructor.getRole() != Role.INSTRUCTOR) {
            throw new RuntimeException("Only instructors can create quizzes.");
        }
        Long courseId = quizRequest.getCourseId();
        Optional<Course> course = courseService.findById(courseId);
        quizService.createQuizFromBank(instructor, quizRequest.getTitle(),quizRequest.getNumQuestions(), quizRequest.getTimeLimit(), course);
        return ResponseEntity.ok("Question bank quiz Created");

    }



    @PostMapping("/create/manual")
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR')")
    public ResponseEntity<?> createQuizByAddingQuestions(@AuthenticationPrincipal User instructor, @RequestBody ManualQuizRequest manualQuizRequest) {
        if (instructor.getRole() != Role.INSTRUCTOR) {
            throw new RuntimeException("Only instructors can create quizzes.");
        }
//        Optional<Course> course = courseService.findById(courseId);
        Long courseId = manualQuizRequest.getCourseid();
        Optional<Course> course = courseService.findById(courseId);
        if (!course.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Course not found");
        }
        quizService.createQuizByAddingQuestions(instructor,manualQuizRequest.getTitle(),manualQuizRequest.getQuestionsNum(), manualQuizRequest.getTimeLimit(),manualQuizRequest.getQuestions(), course);
        return ResponseEntity.ok("Quiz Created");
    }


}
