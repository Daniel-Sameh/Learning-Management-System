package com.swe.lms.AssessmentManagement.entity.Questions;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Entity
@DiscriminatorValue("TRUE_FALSE")
@Setter
@Getter
public class TrueFalseQuestion extends Question {
    @Column(nullable = true)
    private Boolean correctAnswer;
    @Override
    public boolean validateAnswer(Object answer) {
        if (answer instanceof String) {
            return correctAnswer.equals(Boolean.parseBoolean((String) answer));
        } else if (answer instanceof Boolean) {
            return correctAnswer.equals(answer);
        }
        throw new IllegalArgumentException("Invalid answer type for True/False question: " + answer.getClass());
    }
        @Override
    public String getCorrectAnswer() {
        return this.correctAnswer.toString();
    }
}

