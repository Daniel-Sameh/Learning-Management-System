package com.swe.lms.AssessmentManagement.entity.QuestionsFactory;

import com.swe.lms.AssessmentManagement.entity.Questions.MCQOption;
import com.swe.lms.AssessmentManagement.entity.Questions.Question;
import com.swe.lms.AssessmentManagement.entity.Questions.MCQQuestion;
import java.util.List;

public class MCQQuestionFactory implements IQuestionFactory{
    @Override
    public Question createQuestion(String questionText, Object... params){
        MCQQuestion question=new MCQQuestion();
        question.setQuestionText(questionText);
        question.setOptions((List<MCQOption>) params[0]);
        question.setCorrectOptionIndex((int) params[1]);
        question.setScore((float) params[2]);
        return question;

    }
}
