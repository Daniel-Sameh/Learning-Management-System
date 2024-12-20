package com.swe.lms.AssessmentManagement.entity.Questions;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import lombok.Data;


@Entity
@Data
@DiscriminatorValue("SHORT_ANSWER")
public class ShortAnswerQuestion extends Question {

    @Column(nullable = true)
    private String correctAnswer;

    @Override
    public boolean validateAnswer(Object answer) {
        return getCorrectAnswer().equalsIgnoreCase((String) answer);
    }
}
