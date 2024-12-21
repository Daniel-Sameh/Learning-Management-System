package com.swe.lms.AssessmentManagement.entity;

import com.swe.lms.AssessmentManagement.Controller.QuizAnswerRequest;
import com.swe.lms.userManagement.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="quiz_Submissions")
public class QuizSubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name="student_id", nullable=false)
    private User student;

    @ManyToOne
    @JoinColumn(name="quiz_id", nullable=false)
    private Quiz quiz;

    @Column(name="submissionTime")
    private LocalDate submissionTime;

    @Column(name="score")
    private float score;//each student score

    @OneToMany(mappedBy = "quizSubmission", cascade = CascadeType.ALL)
    private List<QuizQuestionAnswers> answers=new ArrayList<>();

    @Column(name="feedback")
    private String feedback;
//

}
