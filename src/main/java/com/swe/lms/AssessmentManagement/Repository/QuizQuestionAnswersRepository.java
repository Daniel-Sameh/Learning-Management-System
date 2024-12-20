package com.swe.lms.AssessmentManagement.Repository;

import com.swe.lms.AssessmentManagement.entity.Questions.Question;
import com.swe.lms.AssessmentManagement.entity.QuizQuestionAnswers;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizQuestionAnswersRepository extends JpaRepository<QuizQuestionAnswers, Long> {
    List<QuizQuestionAnswers> findByQuizIdAndStudentId(Long quizId, Long studentId);

}
