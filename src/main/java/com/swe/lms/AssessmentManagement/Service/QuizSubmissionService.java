package com.swe.lms.AssessmentManagement.Service;

import com.swe.lms.AssessmentManagement.Controller.QuizAnswerRequest;
import com.swe.lms.AssessmentManagement.Repository.*;
import com.swe.lms.AssessmentManagement.entity.Questions.Question;
import com.swe.lms.AssessmentManagement.entity.Quiz;
import com.swe.lms.AssessmentManagement.entity.QuizQuestionAnswers;
import com.swe.lms.AssessmentManagement.entity.QuizSubmission;
import com.swe.lms.exception.ResourceNotFoundException;
import com.swe.lms.userManagement.entity.User;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.swing.text.html.Option;
import java.time.LocalDate;
import java.time.LocalDateTime;
//import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Getter
@Setter
public class QuizSubmissionService {

    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;

    private final QuizSubmissionRepository quizSubmissionRepository;
//    private final QuizResultRepository quizResultRepository;
    private final QuizQuestionAnswersRepository quizQuestionAnswersRepository;

    @Transactional
    public QuizSubmission submitQuiz(Long quizId, User student, List<QuizAnswerRequest> answerRequests){
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found"));
        Optional <QuizSubmission> alreadySubmitted= quizSubmissionRepository.findByQuizAndStudent(quiz, student);
        if(alreadySubmitted.isPresent()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You have already submitted this quiz.");

        }
        LocalDateTime current = LocalDateTime.now();
        LocalDateTime start = quiz.getStartTime();


//        start = start.truncatedTo(ChronoUnit.SECONDS);
//        current = current.truncatedTo(ChronoUnit.SECONDS);

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

        float student_score=0;
        boolean correct;

        for(QuizAnswerRequest answer: answerRequests){

            Question question = questionRepository.findById(answer.getQuestionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

            correct = question.validateAnswer(answer.getAnswer());


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

        float scorePercentage = (student_score / quiz.getFullmark()) * 100;
        if (scorePercentage >= 90) {
            quizSubmission.setFeedback("Excellent");
        } else if (scorePercentage >= 70) {
            quizSubmission.setFeedback("Good");
        } else if (scorePercentage >= 50) {
            quizSubmission.setFeedback("Pass");
        } else {
            quizSubmission.setFeedback("Failed");
        }

//        QuizResult quizResult= new QuizResult();
//        quizResult.setStudent(student);
//        quizResult.setQuiz(quiz);
//        quizResult.setScore(student_score);
//
//        if(student_score==0){
//            quizResult.setFeedback("failed");
//        }else {
//            quizResult.setFeedback("good");//to be edited yet
//
//        }
//        quizResultRepository.save(quizResult);

        quizSubmissionRepository.save(quizSubmission);//updated not saved twice
        return quizSubmission;
    }

    public List<Map<String, Object>> getQuizSubmissions(Long quizId){
        Optional<List<QuizSubmission>> quizSubmissions=quizSubmissionRepository.findAllByQuiz_Id(quizId);
        if (quizSubmissions.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No submissions found for this quiz.");
        }
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found"));
        List<Map<String, Object>> ret = quizSubmissions.get().stream().map(submission -> {
            Map<String, Object> result = new HashMap<>();
            result.put("username", submission.getStudent().getUsername());
            result.put("score", submission.getScore());
            return result;
        }).collect(Collectors.toList());
        int size = quizSubmissions.get().size();
        float total = quizSubmissions.get().stream().map(QuizSubmission::getScore).reduce(0f, Float::sum);
        ret.add(Map.of("average", total / size));
        ret.add(Map.of("fullmark", quiz.getFullmark()));

        return ret;
    }
}
