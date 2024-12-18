package com.swe.lms.AssessmentManagement.Controller;
import com.swe.lms.AssessmentManagement.Service.QuizService;
import com.swe.lms.AssessmentManagement.entity.Quiz;
import com.swe.lms.courseManagement.Service.CourseService;
import com.swe.lms.courseManagement.entity.Course;
import com.swe.lms.userManagement.entity.Role;
import com.swe.lms.userManagement.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/quizzes")
@AllArgsConstructor

public class QuizCreationController {
    private final QuizService quizService;
    private final CourseService courseService;


    @PreAuthorize("hasRole('INSTRUCTOR')")
    @PostMapping("/create-from-bank")
    public Quiz createQuizFromBank(@AuthenticationPrincipal User instructor, @RequestBody QuestionBankQuizRequest quizRequest,@RequestParam Long courseId) {
        if (instructor.getRole() != Role.INSTRUCTOR) {
            throw new RuntimeException("Only instructors can create quizzes.");
        }
        Optional<Course> course = courseService.findById(courseId);
        return quizService.createQuizFromBank(instructor, quizRequest.getTitle(), quizRequest.getNumQuestions(), course);
    }


    @PreAuthorize("hasRole('INSTRUCTOR')")
    @PostMapping("/create-manual")
    public Quiz createQuizByAddingQuestions(@AuthenticationPrincipal User instructor,@RequestBody ManualQuizRequest manualQuizRequest ,@RequestParam Long courseId) {
        if (instructor.getRole() != Role.INSTRUCTOR) {
            throw new RuntimeException("Only instructors can create quizzes.");
        }
        Optional<Course> course = courseService.findById(courseId);
        return quizService.createQuizByAddingQuestions(instructor, manualQuizRequest.getTitle(), manualQuizRequest.getQuestions(),course);
    }

}
