package com.swe.lms.AssessmentManagement.Service;

import com.swe.lms.AssessmentManagement.Repository.QuestionBankRepository;
import com.swe.lms.AssessmentManagement.Repository.QuestionRepository;
import com.swe.lms.AssessmentManagement.entity.Questions.Question;
import com.swe.lms.AssessmentManagement.entity.QuestionBank;

import com.swe.lms.courseManagement.Repository.CourseRepository;
import com.swe.lms.courseManagement.entity.Course;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Getter
@Setter
public class QuestionBankService {

    private final QuestionService questionCreation;
    private final QuestionRepository questionRepository;
    @Autowired
    private QuestionBankRepository questionBankRepository;
    private final CourseRepository courseRepository;



    public void addQuestion(Course course,QuestionRequest questionRequest) {
        Optional<QuestionBank> bankOpt = questionBankRepository.findByCourseId(course.getId());
        QuestionBank questionBank = bankOpt.orElseGet(() -> {
            QuestionBank newBank = new QuestionBank();
            newBank.setCourse(course);
            newBank.setQuestionsNumber(0);
            return questionBankRepository.save(newBank);
        });
        Question question=questionCreation.createQuestion(questionRequest);
        if (question == null) {
            throw new IllegalArgumentException("Invalid question type provided.");
        }
        questionBank.addQuestion(question);
        questionRepository.save(question);
    }
    public void deleteQuestion(Course course,Question question) {
        Optional<QuestionBank> bankOpt = questionBankRepository.findByCourseId(course.getId());
        if(bankOpt.isEmpty()){
            throw new RuntimeException("No bank for this course to delete a question from it.");
        }
        bankOpt.get().removeQuestion(question);
        questionRepository.delete(question);
    }

    public List<Question> getQuestions(long courseId) {
        Optional<QuestionBank> bankOpt = questionBankRepository.findByCourseId(courseId);
        if (bankOpt.isPresent()) {
            return bankOpt.get().getQuestions();
        } else {
            throw new RuntimeException("Question bank not found for the given course ID");
        }
    }

    public void createBankTOCourse(List<QuestionRequest> questionRequests, Course questionBankcourse) {
        if (questionRequests.isEmpty()) {
            throw new IllegalArgumentException("Question request list is empty");
        }

        long questionBankCourseId=questionBankcourse.getId();

        Optional<QuestionBank> bankOpt = questionBankRepository.findByCourseId(questionBankCourseId);

        QuestionBank questionBank = bankOpt.orElseGet(() -> {
            QuestionBank newBank = new QuestionBank();
            newBank.setCourse(questionBankcourse);
            newBank.setQuestionsNumber(0);
            return questionBankRepository.save(newBank);
        });

        for (QuestionRequest request : questionRequests) {
            Optional<Question> existingQuestion = questionRepository.findByQuestionText(request.getQuestionText());
            if (existingQuestion.isPresent()) {
                throw new RuntimeException("Question with this text already exists.");
            }else{
                Question question = questionCreation.createQuestion(request);

                questionBank.addQuestion(question);
            }

        }

        questionBank.setQuestionsNumber(questionBank.getQuestions().size());
        questionBankRepository.save(questionBank);

        System.out.println("Question bank created successfully for course ID: " + questionBankCourseId);
    }

}

