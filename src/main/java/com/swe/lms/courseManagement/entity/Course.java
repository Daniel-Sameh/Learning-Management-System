package com.swe.lms.courseManagement.entity;


import com.swe.lms.AssessmentManagement.entity.QuestionBank;
import com.swe.lms.AssessmentManagement.entity.Questions.Question;
import com.swe.lms.userManagement.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "courses")
public class Course {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "code")
    private String code;

    @ManyToMany
    @JoinTable(
            name = "course_student",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id"))
    private List<User> students;

    @ManyToOne
    @JoinColumn(name = "instructor_id")
    private User instructor;

     @OneToMany(mappedBy = "course")
     private List<Lecture> lectures;

    @OneToMany(mappedBy = "course")
    private List<Post> posts;

    @OneToOne(mappedBy = "course", cascade = CascadeType.ALL)
    private QuestionBank questionBank;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Question> questions = new ArrayList<>();

}
