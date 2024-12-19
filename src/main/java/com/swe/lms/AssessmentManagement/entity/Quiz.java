package com.swe.lms.AssessmentManagement.entity;

import com.swe.lms.AssessmentManagement.entity.Questions.Question;
import com.swe.lms.courseManagement.entity.Course;
import com.swe.lms.userManagement.entity.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="quizzes")
@Data
@Getter
@Setter
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name="title", nullable = false)
    private String title;

    @Column(nullable = false)
    private Integer timeLimit;

    @OneToMany(mappedBy = "quizId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "instructor_id", nullable = false)
    private User instructor;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
    public void addQuestion(Question question) {
        questions.add(question);
    }

    public void removeQuestion(Question question) {
        questions.remove(question);
    }
}
