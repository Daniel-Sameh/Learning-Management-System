package com.swe.lms.AssessmentManagement.entity.QuestionsFactory;

//import com.swe.lms.AssessmentManagement.entity.Questions.MCQOption;
import com.swe.lms.AssessmentManagement.entity.Questions.Question;
import com.swe.lms.AssessmentManagement.entity.Questions.MCQQuestion;
import com.swe.lms.courseManagement.Repository.CourseRepository;
import com.swe.lms.courseManagement.entity.Course;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;
@AllArgsConstructor

public class MCQQuestionFactory implements IQuestionFactory{
    private final CourseRepository courseRepository;
    @Override
    public Question createQuestion(String questionText, long courseid , Object... params){
        MCQQuestion question=new MCQQuestion();
        question.setCourse(courseRepository.findById(courseid).get());
        question.setQuestionText(questionText);
        System.out.println("The options from inside the factory are:");
        System.out.println((List<String>) params[0]);
        question.setOptions((List<String>) params[0]);
        question.setCorrectOptionIndex((int) params[1]);
        question.setScore((float) params[2]);
        return question;

    }
}

