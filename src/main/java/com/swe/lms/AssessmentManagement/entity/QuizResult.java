//package com.swe.lms.AssessmentManagement.entity;
//
//import com.swe.lms.userManagement.entity.User;
//import jakarta.persistence.*;
//import lombok.Data;
//
//@Entity
//@Data
//public class QuizResult {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne
//    @JoinColumn(name = "student_id", nullable = false)
//    private User student;
//
//    @ManyToOne
//    @JoinColumn(name = "quiz_id", nullable = false)
//    private Quiz quiz;
//
//    @Column(name="score")
//    private float score;
//
//    @Lob//field for large object in the database
//    private String feedback;
//
//}
