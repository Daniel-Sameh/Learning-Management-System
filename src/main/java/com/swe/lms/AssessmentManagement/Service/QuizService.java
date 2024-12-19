package com.swe.lms.AssessmentManagement.Service;
import com.swe.lms.AssessmentManagement.Repository.QuestionRepository;
import com.swe.lms.AssessmentManagement.Repository.QuizRepository;
import com.swe.lms.AssessmentManagement.entity.Questions.MCQQuestion;
import com.swe.lms.AssessmentManagement.entity.Questions.Question;
import com.swe.lms.AssessmentManagement.entity.QuestionsFactory.IQuestionFactory;
import com.swe.lms.AssessmentManagement.entity.QuestionsFactory.MCQQuestionFactory;
import com.swe.lms.AssessmentManagement.entity.QuestionsFactory.ShortAnswerQuestionFactory;
import com.swe.lms.AssessmentManagement.entity.QuestionsFactory.TrueFalseQuestionFactory;
import com.swe.lms.AssessmentManagement.entity.Quiz;
import com.swe.lms.courseManagement.entity.Course;
import com.swe.lms.userManagement.entity.Role;
import com.swe.lms.userManagement.entity.User;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
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
    private final QuestionRepository questionRepository;

    private Map<String, IQuestionFactory> questionFactories;

//     Initialize factories map after all dependencies are injected

    @PostConstruct
    public void init() {
        questionFactories = new HashMap<>();
        questionFactories.put("MCQ", new MCQQuestionFactory());
        questionFactories.put("TRUE_FALSE", new TrueFalseQuestionFactory());
        questionFactories.put("SHORT_ANSWER", new ShortAnswerQuestionFactory());
    }

//    public Quiz createQuizFromBank(User instructor, String title, int numQuestions, Optional<Course> course) {
//        if (instructor.getRole() != Role.INSTRUCTOR) {
//            throw new RuntimeException("Only instructors can create quizzes.");
//        }
//
//        List<Question> allQuestions = questionBankService.getQuestions();
//        if (allQuestions.size() < numQuestions) {
//            throw new RuntimeException("Not enough questions in the question bank.");
//        }
//        Course courseEntity = course.orElseThrow(() -> new RuntimeException("Course not found."));
//
//        Collections.shuffle(allQuestions);
//
//        Quiz quiz = new Quiz();
//        quiz.setTitle(title);
//        quiz.setInstructor(instructor);
//        quiz.setCourse(courseEntity);
//        quiz.setQuestions(allQuestions.subList(0, numQuestions));
//
//        return quizRepository.save(quiz);
//    }
//
//    public List<Quiz> getQuizzesByInstructor(User user) {
//        if (user.getRole() != Role.INSTRUCTOR) {
//            throw new RuntimeException("Only instructors can view their quizzes.");
//        }
//        return quizRepository.findByInstructor(user);
//    }
//
//    public Quiz createQuizByAddingQuestions(User instructor, String title, List<Question> questions, Optional<Course> course) {
//        if (instructor.getRole() != Role.INSTRUCTOR) {
//            throw new RuntimeException("Only instructors can create quizzes.");
//        }
//
//        if (course == null) {
//            throw new RuntimeException("Course must be specified.");
//        }
//        Course courseEntity = course.orElseThrow(() -> new RuntimeException("Course not found."));
//
//        Quiz quiz = new Quiz();
//        quiz.setTitle(title);
//        quiz.setInstructor(instructor);
//        quiz.setCourse(courseEntity);
//        quiz.setQuestions(questions);
//
//        return quizRepository.save(quiz);
//    }
    public Quiz createQuizFromBank(User instructor, String title, int numQuestions, Optional<Course> course) {
        if (instructor.getRole() != Role.INSTRUCTOR) {
            throw new RuntimeException("Only instructors can create quizzes.");
        }
        Quiz quiz= new Quiz();
    return quiz;
    }

    public Quiz createQuizByAddingQuestions(User instructor, String title, Integer timeLimit, List<QuestionRequest> questionRequests, Optional<Course> course) {
        if (instructor.getRole() != Role.INSTRUCTOR) {
            throw new RuntimeException("Only instructors can create quizzes.");
        }
        Quiz quiz = new Quiz();
        quiz.setTitle(title);
        quiz.setTimeLimit(timeLimit);
        quiz.setInstructor(instructor);
        Course courseEntity = course.orElseThrow(() -> new RuntimeException("Course not found."));
        quiz.setCourse(courseEntity);
        Quiz savedQuiz = quizRepository.save(quiz);

        for (QuestionRequest request : questionRequests) {
            String questionType = request.getQuestiontype();
            IQuestionFactory factory = questionFactories.get(questionType);
            if (factory != null) {
                if ("MCQ".equals(questionType)) {
                    Question question = factory.createQuestion(
                            request.getQuestionText(),
                            request.getOptions(),
                            request.getCorrectOptionIndex(),
                            request.getScore()
                    );
                    savedQuiz.addQuestion(question);
                    questionRepository.save(question);
                } else if ("TRUE_FALSE".equals(questionType)) {
                    Question question = factory.createQuestion(
                            request.getQuestionText(),
                            request.getCorrectAnswer(),
                            request.getScore()
                    );
                    savedQuiz.addQuestion(question);
                    questionRepository.save(question);
                } else if ("SHORT_ANSWER".equals(questionType)) {
                    Question question = factory.createQuestion(
                            request.getQuestionText(),
                            request.getCorrectAnswer(),
                            request.getScore()
                    );
                    savedQuiz.addQuestion(question);
                    questionRepository.save(question);

                }
            } else {
                throw new RuntimeException("Unknown question type: " + questionType);

            }
        }
        return savedQuiz;
    }


}
