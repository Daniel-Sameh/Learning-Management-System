package com.swe.lms.AssessmentManagement.Controller;

import com.swe.lms.AssessmentManagement.Service.QuestionBankService;
import com.swe.lms.AssessmentManagement.entity.Questions.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/question-bank")
public class QuestionBankController {
    @Autowired
    private QuestionBankService questionBankService;

    @PostMapping("/add")
    public void addQuestion(@RequestBody Question question) {
        questionBankService.addQuestion(question);
    }

    @GetMapping("/getquestions")
    public List<Question> getQuestions() {
        return questionBankService.getQuestions();
    }
}
