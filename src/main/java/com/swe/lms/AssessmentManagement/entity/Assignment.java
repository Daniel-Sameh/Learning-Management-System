package com.swe.lms.AssessmentManagement.entity;

import com.swe.lms.courseManagement.entity.Course;
import com.swe.lms.userManagement.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name="Assignments")
@AllArgsConstructor
@NoArgsConstructor
public class Assignment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name="title",nullable=false)
    private String title;

    @Column(name="description",nullable=false)
    private String description;

    @Column(name="deadline" ,nullable=false)
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name="course_id", nullable = false)
    private Course course;

    @ManyToOne
    @JoinColumn(name="instructor_id", nullable = false)
    private User instructor;

}
