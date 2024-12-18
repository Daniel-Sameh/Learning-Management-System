package com.swe.lms.AssessmentManagement.Repository;

import com.swe.lms.AssessmentManagement.entity.Quiz;
import com.swe.lms.userManagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findByInstructor(User instructor);
}