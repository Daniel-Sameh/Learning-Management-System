package com.swe.lms.AssessmentManagement.entity.QuestionsFactory;

import com.swe.lms.AssessmentManagement.entity.Questions.IQuestion;

public interface IQuestionFactory {
    IQuestion createQuestion(String questionText, Object... params);
}
