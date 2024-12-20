package com.swe.lms.AssessmentManagement.Repository;

import com.swe.lms.AssessmentManagement.entity.Questions.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    Optional<Question> findById(long questionid);
    Optional<Question> findByQuestionText(String questionText);
    //    List<Question> findByCourseId(Long courseId);
//    List<Question> findByQuizId(Long quizId);
}