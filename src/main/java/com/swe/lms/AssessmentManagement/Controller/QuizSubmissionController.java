package com.swe.lms.AssessmentManagement.Controller;

import com.swe.lms.AssessmentManagement.Service.QuizSubmissionService;
import com.swe.lms.AssessmentManagement.entity.Quiz;
import com.swe.lms.AssessmentManagement.entity.QuizSubmission;
import com.swe.lms.userManagement.entity.User;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@AllArgsConstructor

@RestController
@RequestMapping("/quizzes/submission")
public class QuizSubmissionController {
    private final QuizSubmissionService quizSubmissionService;
    @PostMapping("/{quizId}")
    public ResponseEntity<?> submitQuiz(@PathVariable long quizID,
                                        @RequestBody List<QuizAnswerRequest> answerRequests,
                                        @AuthenticationPrincipal User student){

        try{
            QuizSubmission quizSubmission= quizSubmissionService.submitQuiz(quizID, student, answerRequests);
            return ResponseEntity.ok(quizSubmission);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error submitting quiz");
        }
    }

}
