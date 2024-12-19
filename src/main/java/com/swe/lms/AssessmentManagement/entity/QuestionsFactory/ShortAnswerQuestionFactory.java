package com.swe.lms.AssessmentManagement.entity.QuestionsFactory;

import com.swe.lms.AssessmentManagement.entity.Questions.Question;
import com.swe.lms.AssessmentManagement.entity.Questions.ShortAnswerQuestion;

public class ShortAnswerQuestionFactory implements IQuestionFactory {
    @Override
    public Question createQuestion(String questionText, Object... params){
        ShortAnswerQuestion question= new ShortAnswerQuestion();
        question.setQuestionText(questionText);
        question.setCorrectAnswer((String) params[0]);
        question.setScore((float) params[1]);
        return question;
    }
}
