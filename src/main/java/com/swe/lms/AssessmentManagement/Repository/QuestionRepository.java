package com.swe.lms.AssessmentManagement.Repository;

import com.swe.lms.AssessmentManagement.entity.Questions.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
//    List<Question> findByCourseId(Long courseId);
//    List<Question> findByQuizId(Long quizId);
}