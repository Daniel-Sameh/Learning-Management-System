package com.swe.lms.AssessmentManagement.Service;

import com.swe.lms.AssessmentManagement.Repository.QuestionRepository;
import com.swe.lms.AssessmentManagement.entity.Questions.Question;
import com.swe.lms.AssessmentManagement.entity.QuestionsFactory.IQuestionFactory;
import com.swe.lms.AssessmentManagement.entity.QuestionsFactory.MCQQuestionFactory;
import com.swe.lms.AssessmentManagement.entity.QuestionsFactory.ShortAnswerQuestionFactory;
import com.swe.lms.AssessmentManagement.entity.QuestionsFactory.TrueFalseQuestionFactory;
import com.swe.lms.courseManagement.Repository.CourseRepository;
import com.swe.lms.courseManagement.entity.Course;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuestionService {
    @Autowired
    private final QuestionRepository questionRepository;
    private Map<String, IQuestionFactory> questionFactories;
    private final CourseRepository courseRepository;
    @PostConstruct
    public void init() {
        questionFactories = new HashMap<>();
        questionFactories.put("MCQ", new MCQQuestionFactory(courseRepository));
        questionFactories.put("TRUE_FALSE", new TrueFalseQuestionFactory(courseRepository));
        questionFactories.put("SHORT_ANSWER", new ShortAnswerQuestionFactory(courseRepository));
    }
    public Question createQuestion(QuestionRequest request) {
        String questionType = request.getQuestionType();
        System.out.println(request.getCourseid());
        Course course = courseRepository.findById(request.getCourseid())
                .orElseThrow(() -> new RuntimeException("Course not found"));
//        Optional<Question> existingQuestion = questionRepository.findByQuestionText(request.getQuestionText());
//        if (existingQuestion.isPresent()) {
//            throw new RuntimeException("Question with this text already exists.");
//        }
        IQuestionFactory factory = questionFactories.get(questionType);

        if (factory != null) {
            if ("MCQ".equals(questionType)) {
                Question question = factory.createQuestion(
                        request.getQuestionText(),
                        request.getCourseid(),
                        request.getOptions(),
                        request.getCorrectOptionIndex(),
                        request.getScore()
                );
                questionRepository.save(question);
                return question;
            } else if ("TRUE_FALSE".equals(questionType)) {
                Question question = factory.createQuestion(
                        request.getQuestionText(),
                        request.getCourseid(),
                        request.getCorrectAnswer(),
                        request.getScore()
                );
                questionRepository.save(question);
                return question;

            } else if ("SHORT_ANSWER".equals(questionType)) {
                Question question = factory.createQuestion(
                        request.getQuestionText(),
                        request.getCourseid(),
                        request.getCorrectAnswer(),
                        request.getScore()
                );
                questionRepository.save(question);
                return question;

            }
        } else {
            throw new RuntimeException("Unknown question type: " + questionType);

        }
        return null;
    }
    public Optional<Question> findById(Long questionID) {
        return questionRepository.findById(questionID);
    }

}
