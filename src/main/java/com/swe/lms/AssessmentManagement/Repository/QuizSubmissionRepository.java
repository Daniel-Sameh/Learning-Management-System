package com.swe.lms.AssessmentManagement.Repository;

import com.swe.lms.AssessmentManagement.entity.Quiz;
import com.swe.lms.AssessmentManagement.entity.QuizSubmission;
import com.swe.lms.userManagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizSubmissionRepository extends JpaRepository<QuizSubmission, Long> {
    Optional<QuizSubmission> findByQuizAndStudent(Quiz quiz, User student);
<<<<<<< HEAD
    Optional<List<QuizSubmission>> findAllByQuiz_Id(Long quizId);
=======
    List<QuizSubmission> findByStudentId(long studentId);

>>>>>>> 2495c58082009c8b92589f8ca4d6bcb2a3e54207
}
