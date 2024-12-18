package com.swe.lms.AssessmentManagement.entity.Questions;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import lombok.Data;


@Entity
@Data
public class ShortAnswerQuestion implements IQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String questionText;
    private String correctAnswer;
    private float score;

    @Override
    public boolean validateAnswer(Object answer) {
        return correctAnswer.equalsIgnoreCase((String) answer);
    }
}
