package com.swe.lms.AssessmentManagement.entity.Questions;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name="TrueFalseQuestions")
public class TrueFalseQuestion implements IQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String questionText;
    private boolean correctAnswer;
    private float score;

    @Override
    public boolean validateAnswer(Object answer) {
        return correctAnswer == (boolean) answer;
    }
}
