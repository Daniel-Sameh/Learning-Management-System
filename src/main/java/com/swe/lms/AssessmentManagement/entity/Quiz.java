package com.swe.lms.AssessmentManagement.entity;

import com.swe.lms.AssessmentManagement.entity.Questions.Question;
import com.swe.lms.courseManagement.entity.Course;
import com.swe.lms.userManagement.entity.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
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

    @Column(name = "start_time", nullable = false, columnDefinition = "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime startTime;

    @Column(nullable = false)
    private Integer timeLimit;

    @Column(nullable = false)
    private Integer questionsNumber;

    // @OneToMany(mappedBy = "quizId", cascade = CascadeType.ALL)
    // private List<Question> questions = new ArrayList<>();
    
@ManyToMany(cascade = CascadeType.ALL)
@JoinTable(
        name = "quiz_question",
        joinColumns = @JoinColumn(name = "quiz_id"),
        inverseJoinColumns = @JoinColumn(name = "question_id")
)
private List<Question> questions = new ArrayList<>();
    
    @ManyToOne
    @JoinColumn(name = "instructor_id", nullable = false)
    private User instructor;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

      //newly added
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "quiz_student",
            joinColumns = @JoinColumn(name = "quiz_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id"))
    private List<User> students=new ArrayList<>();
    
    @Column(name = "fullmark", nullable = false, columnDefinition = "FLOAT NOT NULL DEFAULT 0")
    private Float fullmark;
    
    public void addQuestion(Question question) {
        questions.add(question);
    }

    public void removeQuestion(Question question) {
        questions.remove(question);
    }
    public void addStudents(List<User> studentsToAdd) {
        this.students.addAll(studentsToAdd);
    }
}

