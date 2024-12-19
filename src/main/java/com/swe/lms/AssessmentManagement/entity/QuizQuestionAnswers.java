package com.swe.lms.AssessmentManagement.entity;

import com.swe.lms.AssessmentManagement.entity.Questions.Question;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="QuizAnswers")
public class QuizQuestionAnswers {//for each question
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name="student_answer")
    private String answer;

    @ManyToOne
    @JoinColumn(name="question_id", nullable = false)
    private Question question;

    @ManyToOne
    @JoinColumn(name = "quiz_submission_id", nullable = false)
    private QuizSubmission quizSubmission;

    @Column(name = "correct")
    private boolean isCorrect;
}
