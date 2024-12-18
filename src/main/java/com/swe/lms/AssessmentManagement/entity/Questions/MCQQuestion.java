package com.swe.lms.AssessmentManagement.entity.Questions;
import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
@Table(name="MCQQuestions")
public class MCQQuestion implements IQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String questionText;

    private List<String> options;

    private String correctAnswer;

    private float score;
    @Override
    public boolean validateAnswer(Object answer) {
        return correctAnswer.equals(answer);
    }
}
