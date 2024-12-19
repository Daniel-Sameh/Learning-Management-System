package com.swe.lms.AssessmentManagement.Service;
import com.swe.lms.AssessmentManagement.Repository.QuizRepository;
import com.swe.lms.AssessmentManagement.entity.Questions.Question;
import com.swe.lms.AssessmentManagement.entity.Quiz;
import com.swe.lms.courseManagement.entity.Course;
import com.swe.lms.userManagement.entity.Role;
import com.swe.lms.userManagement.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class QuizService {

    @Autowired
    private final QuizRepository quizRepository;
    @Autowired
    private final QuestionBankService questionBankService;
    @Autowired
    private final QuestionService questionCreation;


    public Quiz createQuizFromBank(User instructor, String title, Integer questionsNum,Integer timeLimit, Optional<Course> course) {
        if (instructor.getRole() != Role.INSTRUCTOR) {
            throw new RuntimeException("Only instructors can create quizzes.");
        }
        Quiz quiz= new Quiz();
        quiz.setTitle(title);
        quiz.setQuestionsNumber(questionsNum);
        quiz.setTimeLimit(timeLimit);
        quiz.setInstructor(instructor);
        Course courseEntity = course.orElseThrow(() -> new RuntimeException("Course not found."));
        quiz.setCourse(courseEntity);
        List<Question> allQuestions = questionBankService.getQuestions();
        if (allQuestions.size() < questionsNum) {
            throw new RuntimeException("Not enough questions in the question bank.");
        }
        Collections.shuffle(allQuestions);
        quiz.setQuestions(allQuestions.subList(0, questionsNum));
        quizRepository.save(quiz);

        return quiz;
    }

    public Quiz createQuizByAddingQuestions(User instructor, String title, Integer questionsNum,Integer timeLimit, List<QuestionRequest> questionRequests, Optional<Course> course) {
        if (instructor.getRole() != Role.INSTRUCTOR) {
            throw new RuntimeException("Only instructors can create quizzes.");
        }
        Quiz quiz = new Quiz();
        quiz.setTitle(title);
        quiz.setQuestionsNumber(questionsNum);
        quiz.setTimeLimit(timeLimit);
        quiz.setInstructor(instructor);
        Course courseEntity = course.orElseThrow(() -> new RuntimeException("Course not found."));
        quiz.setCourse(courseEntity);
        Quiz savedQuiz = quizRepository.save(quiz);

        for (QuestionRequest request : questionRequests) {
            Question question= questionCreation.createQuestion(request);
            savedQuiz.addQuestion(question);
        }
        return savedQuiz;
    }


}
