package com.swe.lms.AssessmentManagement.entity.QuestionsFactory;

import com.swe.lms.AssessmentManagement.Service.QuestionRequest;
import com.swe.lms.AssessmentManagement.entity.Questions.Question;

public interface IQuestionFactory {
    Question createQuestion(String questionText, Object... params);
}
