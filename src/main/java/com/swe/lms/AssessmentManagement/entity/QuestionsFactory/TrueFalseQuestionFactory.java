package com.swe.lms.AssessmentManagement.entity.QuestionsFactory;

import com.swe.lms.AssessmentManagement.entity.Questions.IQuestion;
import com.swe.lms.AssessmentManagement.entity.Questions.TrueFalseQuestion;

public class TrueFalseQuestionFactory implements IQuestionFactory{
    @Override
    public IQuestion createQuestion(String questionText, Object... params){
        TrueFalseQuestion question = new TrueFalseQuestion();
        question.setQuestionText(questionText);
        question.setCorrectAnswer((boolean) params[0]);
        question.setScore((float) params[1]);
        return question;
    }
}
