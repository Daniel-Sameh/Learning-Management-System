package com.swe.lms.AssessmentManagement.entity;

import com.swe.lms.AssessmentManagement.entity.Questions.Question;
import com.swe.lms.courseManagement.entity.Course;

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
    private List<Question> questions = new ArrayList<>();


    @OneToOne
    @JoinColumn(name="course_id")
    private Course course;

    public void addQuestion(Question question) {
        questions.add(question);
    }

    public void removeQuestion(Question question) {
        questions.remove(question);
    }

}
