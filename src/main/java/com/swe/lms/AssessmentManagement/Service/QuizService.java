package com.swe.lms.AssessmentManagement.Service;
import com.swe.lms.AssessmentManagement.Repository.QuestionRepository;
import com.swe.lms.AssessmentManagement.Repository.QuizRepository;
import com.swe.lms.AssessmentManagement.entity.Questions.Question;
import com.swe.lms.AssessmentManagement.entity.Quiz;
import com.swe.lms.courseManagement.entity.Course;
import com.swe.lms.userManagement.entity.Role;
import com.swe.lms.userManagement.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class QuizService {

    @Autowired
    private final QuizRepository quizRepository;
    @Autowired
    private final QuestionBankService questionBankService;
    @Autowired
    private final QuestionService questionCreation;
    @Autowired
    private final QuestionRepository questionRepository;

    public Quiz createQuizFromBank(User instructor, String title, Integer questionsNum,String startTime,Integer timeLimit, Optional<Course> course) {
        if (instructor.getRole() != Role.INSTRUCTOR) {
            throw new RuntimeException("Only instructors can create quizzes.");
        }
        Quiz quiz= new Quiz();
        quiz.setTitle(title);
        quiz.setQuestionsNumber(questionsNum);
        quiz.setTimeLimit(timeLimit);
        quiz.setInstructor(instructor);
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
            LocalDateTime dateTime = LocalDateTime.parse(startTime, formatter);
            quiz.setStartTime(dateTime);
        } catch (Exception e) {
            throw new RuntimeException("Invalid date format for startTime. Expected format: yyyy-MM-dd HH:mm:ss.SSSSSS");
        }

        Course courseEntity = course.orElseThrow(() -> new RuntimeException("Course not found."));
        quiz.setCourse(course.get());
        List<Question> allQuestions = questionBankService.getQuestions(courseEntity.getId());
        if (allQuestions.size() < questionsNum) {
            throw new RuntimeException("Not enough questions in the question bank.");
        }
        Collections.shuffle(allQuestions);
        quiz.setQuestions(allQuestions.subList(0, questionsNum));
        float totalScore=0;
        for(Question q: quiz.getQuestions()){
            System.out.println("Question: "+q.getQuestionText());
            totalScore+=q.getScore();

        }
        quiz.setFullmark(totalScore);
        quizRepository.save(quiz);

        return quiz;
    }

    public Quiz createQuizByAddingQuestions(User instructor, String title, Integer questionsNum, String startTime,Integer timeLimit, List<QuestionRequest> questionRequests, Optional<Course> course) {
        if (instructor.getRole() != Role.INSTRUCTOR) {
            throw new RuntimeException("Only instructors can create quizzes.");
        }
        Quiz quiz = new Quiz();
        quiz.setTitle(title);
        quiz.setQuestionsNumber(questionsNum);
        quiz.setTimeLimit(timeLimit);
        quiz.setInstructor(instructor);
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
            LocalDateTime dateTime = LocalDateTime.parse(startTime, formatter);
            quiz.setStartTime(dateTime);
        } catch (Exception e) {
            throw new RuntimeException("Invalid date format for startTime. Expected format: yyyy-MM-dd HH:mm:ss.SSSSSS");
        }

        quiz.setCourse(course.get());
        System.out.println("course"+ course);
        try {
            quizRepository.save(quiz);
            System.out.println("Quiz saved successfully.");
        } catch (Exception e) {
            System.out.println("Error saving quiz: " + e.getMessage());
            e.printStackTrace();
        }
        float totalScore=0;

        for (QuestionRequest request : questionRequests) {
//            System.out.println("Creating question: " + request.getQuestionText());
//            System.out.println("Course id: " + request.getCourseid());
//            System.out.println("Question type: " + request.getQuestionType());
//            if (request.getQuestionType().equals("MCQ")){
//                System.out.println("options: "+ request.getOptions());
//                System.out.println("correct option index: "+ request.getCorrectOptionIndex());
//            }else if (request.getQuestionType().equals("TRUE_FALSE")){
//                System.out.println("correct answer: "+ request.getCorrectAnswer());
//            }else if (request.getQuestionType().equals("SHORT_ANSWER")){
//                System.out.println("correct answer: "+ request.getCorrectAnswer());
//            }
//            System.out.println("-------------------------------------------------");
            Optional<Question> existingQuestion = questionRepository.findByQuestionText(request.getQuestionText());
            if (existingQuestion.isPresent()) {
                quiz.addQuestion(existingQuestion.get());
                totalScore+=existingQuestion.get().getScore();
            }else{
                Question question= questionCreation.createQuestion(request);
                quiz.addQuestion(question);
                totalScore+=question.getScore();
            }
        }
        quiz.setFullmark(totalScore);
        quizRepository.save(quiz);
        return quiz;
    }


}
