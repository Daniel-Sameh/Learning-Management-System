package com.swe.lms.AssessmentManagement.entity.QuestionsFactory;

import com.swe.lms.AssessmentManagement.entity.Questions.Question;
import com.swe.lms.AssessmentManagement.entity.Questions.TrueFalseQuestion;
import com.swe.lms.courseManagement.Repository.CourseRepository;
import com.swe.lms.courseManagement.entity.Course;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TrueFalseQuestionFactory implements IQuestionFactory {
    private final CourseRepository courseRepository;

    @Override
    public Question createQuestion(String questionText, long courseid, Object... params) {
        TrueFalseQuestion question = new TrueFalseQuestion();
        question.setQuestionText(questionText);
        question.setCourse(courseRepository.findById(courseid).orElseThrow(() -> new RuntimeException("Course not found")));
        // Convert the param to Boolean explicitly
        question.setCorrectAnswer(Boolean.parseBoolean(params[0].toString()));
        question.setScore((float) params[1]);
        return question;
    }
}
