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



    public void addQuestion(Question question) {
        Optional<QuestionBank> bankOpt = questionBankRepository.findByCourseId(question.getCourse().getId());

        QuestionBank questionBank = bankOpt.orElseGet(() -> {
            QuestionBank newBank = new QuestionBank();
            newBank.setCourse(question.getCourse()); // set the course for the new bank
            newBank.setQuestionsNumber(0); // initialize the question count to 0
            return questionBankRepository.save(newBank); // save and return the new bank
        });

        questionBank.addQuestion(question);
        questionRepository.save(question); // Persist the question in the repository
    }

    public List<Question> getQuestions(long courseId) {
        Optional<QuestionBank> bankOpt = questionBankRepository.findByCourseId(courseId);
        if (bankOpt.isPresent()) {
            return bankOpt.get().getQuestions(); // Get the questions from the bank if present
        } else {
            throw new RuntimeException("Question bank not found for the given course ID");
        }
    }

    public void createBankTOCourse(List<QuestionRequest> questionRequests, Course questionBankcourse) {
        if (questionRequests.isEmpty()) {
            throw new IllegalArgumentException("Question request list is empty");
        }

        long questionBankCourseId=questionBankcourse.getId();
//        for(QuestionRequest questionRequest:questionRequests){
//            if(questionBankCourseId!=questionRequest.getCourseid()){
//                throw new IllegalArgumentException("question "+questionRequest.getQuestionText()+" doesn't relate to this course");
//            }
//        }
//        Optional<Course> qBankCourse = Optional.of(courseRepository.findById(questionBankCourseId)
//                .orElseThrow(() -> new RuntimeException("Course with ID " + questionBankCourseId + " not found")));
//


        // Check if the question bank already exists
        Optional<QuestionBank> bankOpt = questionBankRepository.findByCourseId(questionBankCourseId);

        // If the bank does not exist, create a new one
        QuestionBank questionBank = bankOpt.orElseGet(() -> {
            QuestionBank newBank = new QuestionBank();
            newBank.setCourse(questionBankcourse);
            newBank.setQuestionsNumber(0);
            return questionBankRepository.save(newBank);
        });

        // Add each question to the bank
        for (QuestionRequest request : questionRequests) {
            Optional<Question> existingQuestion = questionRepository.findByQuestionText(request.getQuestionText());
            if (existingQuestion.isPresent()) {
                throw new RuntimeException("Question with this text already exists.");
            }else{
                Question question = questionCreation.createQuestion(request); // Question is already saved in the DB

                questionBank.addQuestion(question);  // Adding the question to the bank
            }

        }

        questionBank.setQuestionsNumber(questionBank.getQuestions().size());
        questionBankRepository.save(questionBank);

        System.out.println("Question bank created successfully for course ID: " + questionBankCourseId);
    }

}

