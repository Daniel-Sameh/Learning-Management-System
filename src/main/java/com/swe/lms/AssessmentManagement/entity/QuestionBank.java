package com.swe.lms.AssessmentManagement.entity;

import com.swe.lms.AssessmentManagement.entity.Questions.IQuestion;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.Getter;


@Entity
@Data
public class QuestionBank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @OneToMany(cascade = CascadeType.ALL)
    private List<IQuestion> questions = new ArrayList<>();

    public void addQuestion(IQuestion question) {
        questions.add(question);
    }

}
