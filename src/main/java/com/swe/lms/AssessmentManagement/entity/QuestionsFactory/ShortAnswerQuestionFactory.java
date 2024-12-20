package com.swe.lms.AssessmentManagement.entity.QuestionsFactory;

import com.swe.lms.AssessmentManagement.entity.Questions.Question;
import com.swe.lms.AssessmentManagement.entity.Questions.ShortAnswerQuestion;
import com.swe.lms.courseManagement.Repository.CourseRepository;
import com.swe.lms.courseManagement.entity.Course;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ShortAnswerQuestionFactory implements IQuestionFactory {
    private final CourseRepository courseRepository;

    @Override
    public Question createQuestion(String questionText, long courseid , Object... params){
        ShortAnswerQuestion question= new ShortAnswerQuestion();
        question.setQuestionText(questionText);
        question.setCourse(courseRepository.findById(courseid).get());
        question.setCorrectAnswer((String) params[0]);
        question.setScore((float) params[1]);
        return question;
    }
}

