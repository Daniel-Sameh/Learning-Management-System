package com.swe.lms.AssessmentManagement.Controller;

import com.swe.lms.AssessmentManagement.Service.QuestionBankService;
import com.swe.lms.AssessmentManagement.Service.QuestionRequest;
import com.swe.lms.AssessmentManagement.entity.Questions.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/question-bank")
public class QuestionBankController {
    @Autowired
    private QuestionBankService questionBankService;

    @PreAuthorize("hasRole('ROLE_INSTRUCTOR')")
    @PostMapping("/create")
    public void createBank(@RequestBody List<QuestionRequest> questionRequests) {
        questionBankService.createBankTOCourse(questionRequests);
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

