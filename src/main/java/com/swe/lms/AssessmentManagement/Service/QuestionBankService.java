package com.swe.lms.AssessmentManagement.Service;

import com.swe.lms.AssessmentManagement.entity.Questions.Question;
import com.swe.lms.AssessmentManagement.entity.QuestionBank;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class QuestionBankService {

    private static QuestionBank questionBank;

    public void addQuestion(Question question) {
        questionBank.addQuestion(question);
    }

    public List<Question> getQuestions() {
        return questionBank.getQuestions();
    }
}
