package com.swe.lms.AssessmentManagement.entity;

import jakarta.persistence.*;

@Entity
@Table(name="Assignments")

public class Assignment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name="description")
    private String description;


}
