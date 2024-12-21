package com.swe.lms.AssessmentManagement.Service;

import com.swe.lms.AssessmentManagement.Controller.QuizAnswerRequest;
import com.swe.lms.AssessmentManagement.Repository.*;
import com.swe.lms.AssessmentManagement.entity.Questions.MCQQuestion;
import com.swe.lms.AssessmentManagement.entity.Questions.Question;
import com.swe.lms.AssessmentManagement.entity.Questions.ShortAnswerQuestion;
import com.swe.lms.AssessmentManagement.entity.Questions.TrueFalseQuestion;
import com.swe.lms.AssessmentManagement.entity.Quiz;
import com.swe.lms.AssessmentManagement.entity.QuizQuestionAnswers;
import com.swe.lms.AssessmentManagement.entity.QuizResult;
import com.swe.lms.AssessmentManagement.entity.QuizSubmission;
import com.swe.lms.exception.ResourceNotFoundException;
import com.swe.lms.userManagement.entity.User;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class QuizSubmissionService {

    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;

    private final QuizSubmissionRepository quizSubmissionRepository;
    private final QuizResultRepository quizResultRepository;
    private final QuizQuestionAnswersRepository quizQuestionAnswersRepository;

    @Transactional
    public QuizSubmission submitQuiz(Long quizId, User student, List<QuizAnswerRequest> answerRequests){
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found"));
        LocalDateTime current = LocalDateTime.now();
        LocalDateTime start = quiz.getStartTime();
        if (current.isBefore(start)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quiz has not started yet.");
        }else if (current.isAfter(start.plusMinutes(quiz.getTimeLimit()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quiz has ended.");
        }
        QuizSubmission quizSubmission=new QuizSubmission();
        quizSubmission.setQuiz(quiz);
        quizSubmission.setStudent(student);
        quizSubmission.setSubmissionTime(LocalDate.now());
        quizSubmissionRepository.save(quizSubmission);
//        if(quizSubmission.getSubmissionTime().){//if submission time exceeded time limit
//
//        }
        float student_score=0;
        boolean correct;
        for(QuizAnswerRequest answer: answerRequests){

            Question question = questionRepository.findById(answer.getQuestionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Question not found"));
//            correct = false;
            correct = question.validateAnswer(answer.getAnswer());
//            if(question instanceof MCQQuestion){
//                MCQQuestion mcq=(MCQQuestion) question;
//                correct= mcq.validateAnswer(answer.getAnswer());
//
//            }
//            else if( question instanceof ShortAnswerQuestion){
//                ShortAnswerQuestion shortAnswerQuestion=(ShortAnswerQuestion) question;
//                correct=shortAnswerQuestion.validateAnswer(answer.getAnswer());
//
//            }
//            else if( question instanceof TrueFalseQuestion){
//                TrueFalseQuestion tfQuestion = (TrueFalseQuestion) question;
//                correct=tfQuestion.validateAnswer(answer.getAnswer());
//
//            }
//            else {
//                throw new IllegalArgumentException("Unknown question type: " + question.getClass());
//            }

            if(correct){
                student_score+=question.getScore();
            }
            System.out.println("Question: " + question.getQuestionText());
            System.out.println("Submitted Answer: " + answer.getAnswer());
            System.out.println("Correct Answer: " + correct);

            QuizQuestionAnswers quizQuestionAnswers=new QuizQuestionAnswers();

            quizQuestionAnswers.setAnswer(answer.getAnswer());
            quizQuestionAnswers.setQuestion(question);
            quizQuestionAnswers.setCorrect(correct);
            quizQuestionAnswers.setQuiz(quiz);
            quizQuestionAnswers.setStudent(student);
            quizQuestionAnswers.setQuizSubmission(quizSubmission);

            quizQuestionAnswersRepository.save(quizQuestionAnswers);

            quizSubmission.getAnswers().add(quizQuestionAnswers);//add the object quiz questions answer to the list in quiz submission
        }
        quizSubmission.setScore(student_score);
        quizSubmissionRepository.save(quizSubmission);//updated not saved twice
        QuizResult quizResult= new QuizResult();
        quizResult.setStudent(student);
        quizResult.setQuiz(quiz);
        quizResult.setScore(student_score);

        if(student_score==0){
            quizResult.setFeedback("failed");
        }else {
            quizResult.setFeedback("good");//to be edited yet

        }
        quizResultRepository.save(quizResult);


        return quizSubmission;
    }
}
