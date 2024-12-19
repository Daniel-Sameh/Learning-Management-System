package com.swe.lms.AssessmentManagement.Service;

import com.swe.lms.AssessmentManagement.Controller.QuizAnswerRequest;
import com.swe.lms.AssessmentManagement.Repository.QuestionRepository;
import com.swe.lms.AssessmentManagement.Repository.QuizRepository;
import com.swe.lms.AssessmentManagement.entity.Questions.MCQQuestion;
import com.swe.lms.AssessmentManagement.entity.Questions.Question;
import com.swe.lms.AssessmentManagement.entity.Questions.ShortAnswerQuestion;
import com.swe.lms.AssessmentManagement.entity.Questions.TrueFalseQuestion;
import com.swe.lms.AssessmentManagement.entity.Quiz;
import com.swe.lms.AssessmentManagement.entity.QuizQuestionAnswers;
import com.swe.lms.AssessmentManagement.entity.QuizSubmission;
import com.swe.lms.userManagement.entity.User;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class QuizSubmissionService {

    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;

    public QuizSubmission submitQuiz(Long quizId, User student, List<QuizAnswerRequest> answerRequests){
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        QuizSubmission quizSubmission=new QuizSubmission();
        quizSubmission.setQuiz(quiz);
        quizSubmission.setStudent(student);
        quizSubmission.setSubmissionTime(LocalDate.now());
//        if(quizSubmission.getSubmissionTime().){
//
//        }
        float student_score=0;
        boolean correct=false;

        for(QuizAnswerRequest answer: answerRequests){
            Question question = questionRepository.findById(answer.getQuestionId())
                    .orElseThrow(() -> new RuntimeException("Question not found"));
            if(question instanceof MCQQuestion){
                MCQQuestion mcq=(MCQQuestion) question;
                correct= mcq.validateAnswer(answer.getAnswer());
            }
            else if( question instanceof ShortAnswerQuestion){
                ShortAnswerQuestion shortAnswerQuestion=(ShortAnswerQuestion) question;
                correct=shortAnswerQuestion.validateAnswer(answer.getAnswer());
            }
            else if( question instanceof TrueFalseQuestion){
                TrueFalseQuestion tfQuestion = (TrueFalseQuestion) question;
                correct=tfQuestion.validateAnswer(answer.getAnswer());
            }
            else {
                throw new IllegalArgumentException("Unknown question type: " + question.getClass());
            }

            if(correct){
                student_score+=question.getScore();
            }
            QuizQuestionAnswers quizQuestionAnswers=new QuizQuestionAnswers();
            quizQuestionAnswers.setAnswer(answer.getAnswer());
            quizQuestionAnswers.setQuestion(question);
            quizQuestionAnswers.setCorrect(correct);
            quizQuestionAnswers.setQuizSubmission(quizSubmission);
            quizSubmission.getAnswers().add(quizQuestionAnswers);//add the object quiz questions answer to the list in quiz submission
        }
        quizSubmission.setScore(student_score);

        return quizSubmission;
    }
}
