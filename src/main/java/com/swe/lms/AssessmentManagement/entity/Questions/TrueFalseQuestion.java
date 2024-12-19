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
    @Column(nullable = false)
    private Boolean correctAnswer;
    @Override
    public boolean validateAnswer(Object answer) {
        return correctAnswer == (boolean) answer;
    }
}
