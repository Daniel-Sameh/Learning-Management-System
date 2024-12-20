package com.swe.lms.AssessmentManagement.Controller;

import com.swe.lms.AssessmentManagement.Service.QuizSubmissionService;
import com.swe.lms.AssessmentManagement.entity.Quiz;
import com.swe.lms.AssessmentManagement.entity.QuizSubmission;
import com.swe.lms.userManagement.entity.User;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@AllArgsConstructor

@RestController
@RequestMapping("/api/quizzes/submission")
public class QuizSubmissionController {
    private static final Logger logger = LoggerFactory.getLogger(QuizSubmissionController.class);

    @Autowired
    private final QuizSubmissionService quizSubmissionService;


    @PostMapping("/{quizId}")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ResponseEntity<?> submitQuiz(@PathVariable("quizId") long quizID,
                                        @RequestBody List<QuizAnswerRequest> answerRequests,
                                        @AuthenticationPrincipal User student){

        try{
            quizSubmissionService.submitQuiz(quizID, student, answerRequests);
            return ResponseEntity.ok("quiz submitted successfully");
        } catch (Exception e) {
            logger.error("Error occurred while submitting quiz: {}", e.getMessage(), e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error submitting quiz");
        }
    }

}
