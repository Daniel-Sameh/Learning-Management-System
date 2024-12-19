package com.swe.lms.AssessmentManagement.entity.Questions;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class MCQOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String optionText;
}
