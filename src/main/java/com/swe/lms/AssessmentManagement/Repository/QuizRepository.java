package com.swe.lms.AssessmentManagement.Repository;

import com.swe.lms.AssessmentManagement.entity.Quiz;
import com.swe.lms.userManagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findQuizzesByInstructorId(long instructorId);
    List<Quiz> findQuizzesByCourseId(long courseId);
    @Override
    Optional<Quiz> findById(Long quizId);
}
