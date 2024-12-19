package com.swe.lms.AssessmentManagement.entity.QuestionsFactory;

import com.swe.lms.AssessmentManagement.entity.Questions.Question;
import com.swe.lms.AssessmentManagement.entity.Questions.TrueFalseQuestion;
import com.swe.lms.courseManagement.entity.Course;

public class TrueFalseQuestionFactory implements IQuestionFactory{
    @Override
    public Question createQuestion(String questionText, Course course , Object... params){
        TrueFalseQuestion question = new TrueFalseQuestion();
        question.setQuestionText(questionText);
        question.setCourse(course);
        question.setCorrectAnswer((boolean) params[0]);
        question.setScore((float) params[1]);
        return question;
    }
}