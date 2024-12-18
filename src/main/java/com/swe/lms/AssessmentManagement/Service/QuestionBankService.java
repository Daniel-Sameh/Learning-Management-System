package com.swe.lms.AssessmentManagement.Service;

import com.swe.lms.AssessmentManagement.entity.Questions.IQuestion;
import com.swe.lms.AssessmentManagement.entity.QuestionBank;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class QuestionBankService {
    private final QuestionBank questionBank;

    public void addQuestion(IQuestion question) {
        questionBank.addQuestion(question);
    }

    public List<IQuestion> getQuestions() {
        return questionBank.getQuestions();
    }
}
