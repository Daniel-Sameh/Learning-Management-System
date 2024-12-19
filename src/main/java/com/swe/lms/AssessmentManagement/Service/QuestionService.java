package com.swe.lms.AssessmentManagement.Service;

import com.swe.lms.AssessmentManagement.Repository.QuestionRepository;
import com.swe.lms.AssessmentManagement.entity.Questions.Question;
import com.swe.lms.AssessmentManagement.entity.QuestionsFactory.IQuestionFactory;
import com.swe.lms.AssessmentManagement.entity.QuestionsFactory.MCQQuestionFactory;
import com.swe.lms.AssessmentManagement.entity.QuestionsFactory.ShortAnswerQuestionFactory;
import com.swe.lms.AssessmentManagement.entity.QuestionsFactory.TrueFalseQuestionFactory;
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

    @PostConstruct
    public void init() {
        questionFactories = new HashMap<>();
        questionFactories.put("MCQ", new MCQQuestionFactory());
        questionFactories.put("TRUE_FALSE", new TrueFalseQuestionFactory());
        questionFactories.put("SHORT_ANSWER", new ShortAnswerQuestionFactory());
    }
    public Question createQuestion(QuestionRequest request) {
        String questionType = request.getQuestiontype();
        IQuestionFactory factory = questionFactories.get(questionType);
        if (factory != null) {
            if ("MCQ".equals(questionType)) {
                Question question = factory.createQuestion(
                        request.getQuestionText(),
                        request.getCourse(),
                        request.getOptions(),
                        request.getCorrectOptionIndex(),
                        request.getScore()
                );
                questionRepository.save(question);
            } else if ("TRUE_FALSE".equals(questionType)) {
                Question question = factory.createQuestion(
                        request.getQuestionText(),
                        request.getCourse(),
                        request.getCorrectAnswer(),
                        request.getScore()
                );
                questionRepository.save(question);
            } else if ("SHORT_ANSWER".equals(questionType)) {
                Question question = factory.createQuestion(
                        request.getQuestionText(),
                        request.getCourse(),
                        request.getCorrectAnswer(),
                        request.getScore()
                );
                questionRepository.save(question);
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
