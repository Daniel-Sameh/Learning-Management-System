package com.swe.lms.AssessmentManagement.entity.Questions;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Entity
@DiscriminatorValue("MCQ")
@Setter
@Getter
public class MCQQuestion extends Question {

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "question_id")
    private List<MCQOption> options;

    @Column(nullable = false)
    private Integer correctOptionIndex;

    @Override
    public boolean validateAnswer(Object answer) {
        return correctOptionIndex.equals(answer);
    }
}
