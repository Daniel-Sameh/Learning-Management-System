package com.swe.lms.AssessmentManagement.entity;

import com.swe.lms.userManagement.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="assignment_Submissions")
public class AssignmentSubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name="student_id", nullable=false)
    private User student;

    @Column(name = "media", nullable = true)
    private String media;

    @Column(name="submission time")
    private LocalDateTime submissionTime;

    @ManyToOne
    @JoinColumn(name = "assignment_id", nullable=false)
    private Assignment assignment;

    @Column(name="status")//graded aw submitted bas
    private String status;

    @Column(name="grade", nullable = true)
    private float grade;
}
