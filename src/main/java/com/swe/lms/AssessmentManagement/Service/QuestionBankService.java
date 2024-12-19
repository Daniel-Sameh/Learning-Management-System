package com.swe.lms.AssessmentManagement.Service;

import com.swe.lms.AssessmentManagement.Repository.QuestionRepository;
import com.swe.lms.AssessmentManagement.entity.Questions.Question;
import com.swe.lms.AssessmentManagement.entity.QuestionBank;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class QuestionBankService {

    private static QuestionBank questionBank;
    private final QuestionService questionCreation;
    private final QuestionRepository questionRepository;

    public void addQuestion(Question question) {
        questionBank.addQuestion(question);
        questionRepository.save(question);
    }

    public List<Question> getQuestions() {
        return questionBank.getQuestions();
    }

    public void createBankTOCourse(List<QuestionRequest> questionRequests){
        for(QuestionRequest request:questionRequests){
            Question question= questionCreation.createQuestion(request);
            questionBank.addQuestion(question);
        }
        System.out.println(" Bank created successfully");
    }

}
