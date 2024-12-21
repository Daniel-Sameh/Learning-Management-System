package com.swe.lms.AssessmentManagement.entity;

import com.swe.lms.courseManagement.entity.Course;
import com.swe.lms.userManagement.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name="Assignments")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Assignment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name="title",nullable=false)
    private String title;

    @Column(name="description",nullable=false)
    private String description;

    @Column(name="deadline" ,nullable=false)
    private LocalDateTime deadline;

    @ManyToOne
    @JoinColumn(name="course_id", nullable = false)
    private Course course;

    @ManyToOne
    @JoinColumn(name="instructor_id", nullable = false)
    private User instructor;

}
