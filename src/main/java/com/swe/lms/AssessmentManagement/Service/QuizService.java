package com.swe.lms.AssessmentManagement.Service;
import com.swe.lms.AssessmentManagement.Mapper.QuizMapper;
import com.swe.lms.AssessmentManagement.Repository.QuestionRepository;
import com.swe.lms.AssessmentManagement.Repository.QuizRepository;
import com.swe.lms.AssessmentManagement.dto.QuizDto;
import com.swe.lms.AssessmentManagement.entity.Questions.Question;
import com.swe.lms.AssessmentManagement.entity.Quiz;
import com.swe.lms.courseManagement.Repository.CourseRepository;
import com.swe.lms.courseManagement.Service.CourseService;
import com.swe.lms.courseManagement.entity.Course;
import com.swe.lms.notification.service.NotificationService;
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

    @Autowired
    private final CourseRepository courseRepository;
    @Autowired
    private final QuizMapper quizMapper;
    @Autowired
    private final NotificationService notificationService;

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
        //get enrolled students in this course add them to quiz students list
        List<User> students = courseEntity.getStudents();
        if (students == null || students.isEmpty()) {
            throw new RuntimeException("No students enrolled in the course.");
        }

        quiz.addStudents(students);
        //heere notify them
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
//        try {
//            quizRepository.save(quiz);
//            System.out.println("Quiz saved successfully.");
//        } catch (Exception e) {
//            System.out.println("Error saving quiz: " + e.getMessage());
//            e.printStackTrace();
//        }
        float totalScore=0;

        for (QuestionRequest request : questionRequests) {
            System.out.println("Creating question: " + request.getQuestionText());
            System.out.println("Course id: " + request.getCourseid());
            System.out.println("Question type: " + request.getQuestionType());
            if (request.getQuestionType().equals("MCQ")){
                System.out.println("options: "+ request.getOptions());
                System.out.println("correct option index: "+ request.getCorrectOptionIndex());
            }else if (request.getQuestionType().equals("TRUE_FALSE")){
                System.out.println("correct answer: "+ request.getCorrectAnswer());
            }else if (request.getQuestionType().equals("SHORT_ANSWER")){
                System.out.println("correct answer: "+ request.getCorrectAnswer());
            }
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
        List<User> students = course.get().getStudents();
        if (students == null || students.isEmpty()) {
            throw new RuntimeException("No students enrolled in the course.");
        }
        quiz.addStudents(students);
        System.out.println("-------------------------------------------------");
        quiz.setFullmark(totalScore);
        System.out.println("-------------------------------------------------");
        quizRepository.save(quiz);
        return quiz;
    }

    public Quiz updateQuiz(Quiz quiz,String title, Integer questionsNum, String startTime,Integer timeLimit, Optional<Course> course){
        System.out.println("--------------inside update-------");
        quiz.setTitle(title);
        quiz.setCourse(course.get());
        quiz.setQuestionsNumber(questionsNum);
        quiz.setTimeLimit(timeLimit);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
        LocalDateTime dateTime = LocalDateTime.parse(startTime, formatter);

        quiz.setStartTime(dateTime);
        quizRepository.save(quiz);
        return  quiz;
    }

    public List<QuizDto> getQuizzesByStudentId(long studentId){
        List<Course> courses=courseRepository.findByStudentsId(studentId);;
        if(courses.isEmpty()){
            throw new RuntimeException("Student is not enrolled in any courses.");
        }
        List<QuizDto> quizDtos=new ArrayList<>();
        for(Course course: courses){
            List<Quiz> quizzes=quizRepository.findQuizzesByCourseId(course.getId());
            for (Quiz quiz : quizzes) {
                quizDtos.add(quizMapper.toDTO(quiz));
            }


        }
        return quizDtos;
    }
    public List<QuizDto> getQuizzesByCourseId(Long courseId){
        List<Quiz> quizzes=quizRepository.findQuizzesByCourseId(courseId);
        List<QuizDto> quizDtos=new ArrayList<>();
        for (Quiz quiz : quizzes) {
            quizDtos.add(quizMapper.toDTO(quiz));
        }
        return quizDtos;
    }

//    public void notify(String s, String s1, Quiz quiz) {
//    }
    public void notify(String subject, String body, Quiz quiz){
        List<User> students = quiz.getStudents();
        notificationService.sendNotification(students, subject, body);
    }


}
