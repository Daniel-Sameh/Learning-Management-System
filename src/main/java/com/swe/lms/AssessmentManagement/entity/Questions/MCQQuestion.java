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

    @ElementCollection
//    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
//    @JoinColumn(name = "question_id")
    @CollectionTable(name = "mcq_options", joinColumns = @JoinColumn(name = "question_id"))
    @Column(name = "option")
    private List<String> options;

    @Column(nullable = true)
    private Integer correctOptionIndex;

    @Override
    public boolean validateAnswer(Object answer) {
        try {
            int answerIndex = Integer.parseInt(answer.toString());
            return correctOptionIndex.equals(answerIndex);
        } catch (NumberFormatException e) {
            return false;
        }
    }
    @Override
    public String getCorrectAnswer() {
        return this.correctOptionIndex.toString();
    }
}
