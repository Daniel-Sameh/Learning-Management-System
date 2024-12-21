package com.swe.lms.AssessmentManagement.Controller;

import com.swe.lms.AssessmentManagement.Service.QuestionBankService;
import com.swe.lms.AssessmentManagement.Service.QuestionRequest;
import com.swe.lms.AssessmentManagement.entity.Questions.Question;
import com.swe.lms.courseManagement.Repository.CourseRepository;
import com.swe.lms.courseManagement.entity.Course;
import com.swe.lms.userManagement.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/Qbank")
public class QuestionBankController {
    @Autowired
    private QuestionBankService questionBankService;
    @Autowired
    private CourseRepository courseRepository;
    @PostMapping("/create")
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR')")
    public ResponseEntity<?> createBank(@AuthenticationPrincipal User instructor, @RequestBody QuestionBankRequest questionBankRequest) {
        Optional<Course> course=courseRepository.findById(questionBankRequest.getCourseid());
        if (course.isEmpty()) { // not sure
            return ResponseEntity.status(404).body("Course not found");
        }
        questionBankService.createBankTOCourse(questionBankRequest.getQuestions(),course.get());
        return ResponseEntity.ok("Question bank Created");

    }

//    @PreAuthorize("hasRole('ROLE_INSTRUCTOR')")
//    @PostMapping("/add")
//    public void addQuestion(@RequestBody Question question) {
//        questionBankService.addQuestion(question);
//    }
//
//    @PreAuthorize("hasRole('ROLE_INSTRUCTOR')")
//    @GetMapping("/getquestions")
//    public List<Question> getQuestions() {
//        return questionBankService.getQuestions();
//    }
}

