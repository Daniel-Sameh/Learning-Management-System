package com.swe.lms.AssessmentManagement.entity;

import com.swe.lms.AssessmentManagement.entity.Questions.IQuestion;
import com.swe.lms.courseManagement.entity.Course;
import com.swe.lms.userManagement.entity.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="Quizzes")
@Data
@Getter
@Setter
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @OneToMany(cascade = CascadeType.ALL)
    private List<IQuestion> questions = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "instructor_id", nullable = false)
    private User instructor;

    @Column(name="title")
    private String title;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
    public void addQuestion(IQuestion question) {
        questions.add(question);
    }

    public void removeQuestion(IQuestion question) {
        questions.remove(question);
    }
}
