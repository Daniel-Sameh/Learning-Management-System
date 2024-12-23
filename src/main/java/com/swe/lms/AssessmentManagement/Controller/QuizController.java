package com.swe.lms.AssessmentManagement.Controller;
import com.swe.lms.AssessmentManagement.Mapper.QuizMapper;
import com.swe.lms.AssessmentManagement.Repository.QuizRepository;
import com.swe.lms.AssessmentManagement.Service.QuizService;
import com.swe.lms.AssessmentManagement.dto.QuizDto;
import com.swe.lms.AssessmentManagement.dto.QuizSubmissionDto;
import com.swe.lms.AssessmentManagement.entity.Quiz;
import com.swe.lms.AssessmentManagement.entity.QuizSubmission;
import com.swe.lms.courseManagement.Repository.CourseRepository;
import com.swe.lms.courseManagement.Service.CourseService;
import com.swe.lms.courseManagement.entity.Course;
import com.swe.lms.courseManagement.entity.Post;
import com.swe.lms.userManagement.entity.Role;
import com.swe.lms.userManagement.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/quizzes")
@AllArgsConstructor

public class QuizController {
    private final QuizService quizService;
    private final CourseService courseService;
    private final QuizRepository quizRepository;
    private final QuizMapper quizMapper;
    private final CourseRepository courseRepository;




    @PostMapping("/create/bank")
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR')")
    public ResponseEntity<?> createQuizFromBank(@AuthenticationPrincipal User instructor, @RequestBody QuestionBankQuizRequest quizRequest) {
        if (instructor.getRole() != Role.INSTRUCTOR) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Only instructors can create quizzes.");
        }
        Optional<Course> course = courseRepository.findById(quizRequest.getCourseId());
        if (course.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Course not found");
        }
        if (instructor.getRole().toString().equals("INSTRUCTOR")){
            if (course.get().getInstructor().getId() != instructor.getId()) {
                return ResponseEntity.status(403).body("You are not authorized to create a quiz.");
            }
        }
        Quiz quiz=quizService.createQuizFromBank(instructor, quizRequest.getTitle(),quizRequest.getNumQuestions(),quizRequest.getStartTime(), quizRequest.getTimeLimit(), course);

        quizService.notify(
                "New Announcement: \"" + course.get().getName() + "\"",
                "Title: New Question Bank Quiz "+quiz.getTitle()  + "<br>Content: This quiz will be held on "+ quiz.getStartTime()+". You will have "+quiz.getTimeLimit()+" minutes till the end of the quiz."+", It consists of " + quiz.getQuestionsNumber()+ " questions. "
                        +"<br> Best of luck.",
                quiz
        );
        return ResponseEntity.ok("Question bank quiz Created");

    }


    @PostMapping("/create/manual")
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR')")
    public ResponseEntity<?> createQuizByAddingQuestions(@AuthenticationPrincipal User instructor, @RequestBody ManualQuizRequest manualQuizRequest) {
        if (instructor.getRole() != Role.INSTRUCTOR) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Only instructors can create quizzes.");
        }
        Optional<Course> course = courseRepository.findById(manualQuizRequest.getCourseid());
        if (course.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Course not found");
        }
        if (instructor.getRole().toString().equals("INSTRUCTOR")){
            if (course.get().getInstructor().getId() != instructor.getId()) {
                return ResponseEntity.status(403).body("You are not authorized to create a quiz.");
            }
        }
        Quiz quiz=quizService.createQuizByAddingQuestions(instructor,manualQuizRequest.getTitle(),manualQuizRequest.getQuestionsNum(),manualQuizRequest.getStartTime(), manualQuizRequest.getTimeLimit(),manualQuizRequest.getQuestions(), course);
        quizService.notify(
                "New Announcement: \"" + course.get().getName() + "\"",
                "Title: New Quiz "+quiz.getTitle()  + "<br>Content: This quiz will be held on "+ quiz.getStartTime()+". You will have "+quiz.getTimeLimit()+" minutes till the end of the quiz."+", It consists of " + quiz.getQuestionsNumber()+ " questions. "
                        +"<br> Best of luck.",
                quiz
        );
        return ResponseEntity.ok("Quiz Created");
    }

    @GetMapping("/get/{quizId}")
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_STUDENT')")
    public ResponseEntity<?> getQuiz(@AuthenticationPrincipal User user, @PathVariable Long quizId) {
        Optional<Quiz> quiz = quizRepository.findById(quizId);
        if (quiz.isEmpty()) {
            return ResponseEntity.status(404).body("No quiz with this id");
        }
        if (user.getRole().toString().equals("STUDENT") && !quiz.get().getCourse().getStudents().contains(user)) {
            return ResponseEntity.status(403).body(null);
        }else if (user.getRole().toString().equals("INSTRUCTOR") && !quiz.get().getInstructor().equals(user)) {
            return ResponseEntity.status(403).body(null);
        }
        QuizDto quizDto= QuizMapper.toDTO(quiz.get());
        return ResponseEntity.ok(quizDto);
    }

    @GetMapping("/get/course/{courseId}")
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_STUDENT')")
    public ResponseEntity<?> getQuizzesByCourse(@PathVariable long courseId) {
        List<Quiz> quizzes= quizRepository.findQuizzesByCourseId(courseId);
        if(quizzes.isEmpty()){
            return ResponseEntity.status(404).body("No quizzes for this Course");
        }
        List<QuizDto> quizDtos=new ArrayList<>();
        for (Quiz quiz : quizzes) {
            quizDtos.add(quizMapper.toDTO(quiz));
        }
        return ResponseEntity.ok(quizDtos);

    }

    @GetMapping("/get/instructor/{instructorId}")
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR')")
    public ResponseEntity<?> getQuizzesByInstructor(@PathVariable long instructorId){
        List<Quiz> quizzes= quizRepository.findQuizzesByInstructorId(instructorId);
        if(quizzes.isEmpty()){
            return ResponseEntity.status(404).body("No quizzes made by this instructor");
        }
        List<QuizDto> quizDtos=new ArrayList<>();
        for (Quiz quiz : quizzes) {
            quizDtos.add(quizMapper.toDTO(quiz));
        }
        return ResponseEntity.ok(quizDtos);
    }

    @GetMapping("/get/student/{studentId}")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ResponseEntity<?> getQuizzesForStudent(@PathVariable long studentId){
        List<QuizDto> quizDtos=quizService.getQuizzesByStudentId(studentId);
        if(quizDtos.isEmpty()){
            return ResponseEntity.status(404).body("No quizzes associated with this student");
        }
        return ResponseEntity.ok(quizDtos);
    }

    @DeleteMapping("/delete/{quizId}")
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR')")
    public ResponseEntity<?> deleteQuiz(@AuthenticationPrincipal User instructor,@PathVariable long quizId){
        Optional<Quiz> quiz= quizRepository.findById(quizId);
        if(quiz.isEmpty()){
            return ResponseEntity.status(404).body("No quiz with this id");
        }
        Optional<Course> course = courseRepository.findById(quiz.get().getCourse().getId());
        if (course.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Course not found");
        }
        if (quiz.get().getInstructor().getRole().toString().equals("INSTRUCTOR")){
            if (quiz.get().getInstructor().getId() != instructor.getId()) {
                return ResponseEntity.status(403).body("You are not authorized to create a quiz.");
            }
        }
        quizRepository.delete(quiz.get());
        return ResponseEntity.ok("Quiz deleted successfully");
    }

    @PutMapping("/update/{quizId}")
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR')")
    public ResponseEntity<?> updateQuiz(@AuthenticationPrincipal User instructor, @PathVariable long quizId, @RequestBody QuizUpdateRequest updateRequest){

        Optional<Quiz> quizOptional= quizRepository.findById(quizId);
        if(quizOptional.isEmpty()){
            return ResponseEntity.status(404).body("No quiz with this id");
        }

        Quiz quiz=quizOptional.get();
        Optional<Course> course = courseRepository.findById(quiz.getCourse().getId());
        if (course.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Course not found");
        }
        if (quiz.getInstructor().getRole().toString().equals("INSTRUCTOR")){
            if (quiz.getInstructor().getId() != instructor.getId()) {
                return ResponseEntity.status(403).body("You are not authorized to create a quiz.");
            }
        }
        quiz= quizService.updateQuiz(quiz,updateRequest.getTitle(),updateRequest.getQuestionsNum(), updateRequest.getStartTime(), updateRequest.getTimeLimit(), course);
        quizService.notify(
                "New Announcement: \"" + course.get().getName() + "\"",
                "Title: The quiz"+quiz.getTitle() +" was updated. " + "<br>Content: This quiz will be held on "+ quiz.getStartTime()+". You will have "+quiz.getTimeLimit()+" minutes till the end of the quiz."+", It consists of " + quiz.getQuestionsNumber()+ " questions. "
                        +"<br> Best of luck.",
                quiz
        );
        return ResponseEntity.ok("Quiz updated successfully");
    }

}
