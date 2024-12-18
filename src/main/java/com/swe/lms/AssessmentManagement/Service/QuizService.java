package com.swe.lms.AssessmentManagement.Service;
import com.swe.lms.AssessmentManagement.Repository.QuizRepository;
import com.swe.lms.AssessmentManagement.entity.Questions.IQuestion;
import com.swe.lms.AssessmentManagement.entity.Quiz;
import com.swe.lms.courseManagement.entity.Course;
import com.swe.lms.userManagement.entity.Role;
import com.swe.lms.userManagement.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class QuizService {

    private final QuizRepository quizRepository;
    private final QuestionBankService questionBankService;


    public Quiz createQuizFromBank(User instructor, String title, int numQuestions, Optional<Course> course) {
        if (instructor.getRole() != Role.INSTRUCTOR) {
            throw new RuntimeException("Only instructors can create quizzes.");
        }

        List<IQuestion> allQuestions = questionBankService.getQuestions();
        if (allQuestions.size() < numQuestions) {
            throw new RuntimeException("Not enough questions in the question bank.");
        }
        Course courseEntity = course.orElseThrow(() -> new RuntimeException("Course not found."));

        Collections.shuffle(allQuestions);

        Quiz quiz = new Quiz();
        quiz.setTitle(title);
        quiz.setInstructor(instructor);
        quiz.setCourse(courseEntity);
        quiz.setQuestions(allQuestions.subList(0, numQuestions));

        return quizRepository.save(quiz);
    }

    public List<Quiz> getQuizzesByInstructor(User user) {
        if (user.getRole() != Role.INSTRUCTOR) {
            throw new RuntimeException("Only instructors can view their quizzes.");
        }
        return quizRepository.findByInstructor(user);
    }

    public Quiz createQuizByAddingQuestions(User instructor, String title, List<IQuestion> questions, Optional<Course> course) {
        if (instructor.getRole() != Role.INSTRUCTOR) {
            throw new RuntimeException("Only instructors can create quizzes.");
        }

        if (course == null) {
            throw new RuntimeException("Course must be specified.");
        }
        Course courseEntity = course.orElseThrow(() -> new RuntimeException("Course not found."));

        Quiz quiz = new Quiz();
        quiz.setTitle(title);
        quiz.setInstructor(instructor);
        quiz.setCourse(courseEntity);
        quiz.setQuestions(questions);

        return quizRepository.save(quiz);
    }

}
