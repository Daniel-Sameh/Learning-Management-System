package com.swe.lms.AssessmentManagement.entity.Questions;

import com.swe.lms.AssessmentManagement.entity.Quiz;
import com.swe.lms.courseManagement.entity.Course;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "question_type")
@Setter
@Getter
public abstract class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name= "question_text", nullable = false)
    private String questionText;


    @Column(name = "score")
    private float score;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;


    // @ManyToOne
    // @JoinColumn(name = "quiz_id")
    // private Quiz quizId;
    
    @ManyToMany(mappedBy = "questions") 
    private List<Quiz> quizzes = new ArrayList<>();
    
    public abstract boolean  validateAnswer(Object answer);
    public abstract String getCorrectAnswer();

}
