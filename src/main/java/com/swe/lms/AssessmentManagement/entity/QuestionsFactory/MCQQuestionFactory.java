package com.swe.lms.AssessmentManagement.entity.QuestionsFactory;

import com.swe.lms.AssessmentManagement.entity.Questions.IQuestion;
import com.swe.lms.AssessmentManagement.entity.Questions.MCQQuestion;
import java.util.List;

public class MCQQuestionFactory implements IQuestionFactory{
    @Override
    public IQuestion createQuestion(String questionText, Object... params){
        MCQQuestion question=new MCQQuestion();
        question.setQuestionText(questionText);
        question.setOptions((List<String>) params[0]);
        question.setCorrectAnswer((String) params[1]);
        question.setScore((float) params[2]);
        return question;

    }
}
