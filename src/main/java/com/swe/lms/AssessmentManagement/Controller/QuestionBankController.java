package com.swe.lms.AssessmentManagement.Controller;

import com.swe.lms.AssessmentManagement.Mapper.QuestionMapper;
import com.swe.lms.AssessmentManagement.Repository.QuestionRepository;
import com.swe.lms.AssessmentManagement.Service.QuestionBankService;
import com.swe.lms.AssessmentManagement.Service.QuestionRequest;
import com.swe.lms.AssessmentManagement.dto.QuestionDto;
import com.swe.lms.AssessmentManagement.entity.QuestionBank;
import com.swe.lms.AssessmentManagement.entity.Questions.Question;
import com.swe.lms.courseManagement.Repository.CourseRepository;
import com.swe.lms.courseManagement.entity.Course;
import com.swe.lms.userManagement.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/Qbank")
public class QuestionBankController {
    @Autowired
    private QuestionBankService questionBankService;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private QuestionRepository questionRepository;


    @PostMapping("/create")
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR')")
    public ResponseEntity<?> createBank(@AuthenticationPrincipal User instructor, @RequestBody QuestionBankRequest questionBankRequest) {
        Optional<Course> course=courseRepository.findById(questionBankRequest.getCourseid());
        if (course.isEmpty()) {
            return ResponseEntity.status(404).body("Course not found");
        }
        questionBankService.createBankTOCourse(questionBankRequest.getQuestions(),course.get());
        return ResponseEntity.ok("Question bank Created");
    }

    @PostMapping("/{courseId}/addQuestion")
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR')")
    public ResponseEntity<String> addQuestion(@PathVariable long courseId, @RequestBody QuestionRequest question) {
            Optional<Course> course= courseRepository.findById(courseId);
            if (course.isEmpty()) {
                return ResponseEntity.status(404).body("Course not found");
            }
            questionBankService.addQuestion(course.get(),question);
            return ResponseEntity.ok("Question added");
    }

    @DeleteMapping("/{courseId}/Question/{questionId}")
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR')")
    public ResponseEntity<String> deleteQuestion(@PathVariable long courseId, @PathVariable long questionId) {
        Optional<Course> course= courseRepository.findById(courseId);
        if (course.isEmpty()) {
            return ResponseEntity.status(404).body("Course not found");
        }
        Optional<Question> question=questionRepository.findById(questionId);
        if (question.isEmpty()) {
            return ResponseEntity.status(404).body("Question not found.");
        }
        if(question.get().getCourse().getId() != courseId){
            return ResponseEntity.status(404).body("This question doesn't belong to this course");
        }
        questionBankService.deleteQuestion(course.get(),question.get());
        return ResponseEntity.ok("Question removed");
    }

    @GetMapping("/{courseId}/getquestions")
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_STUDENT')")
    public ResponseEntity<?> getQuestionsByCourse(@PathVariable long courseId){
        Optional<Course> course= courseRepository.findById(courseId);
        if (course.isEmpty()) {
            return ResponseEntity.status(404).body("Course not found");
        }
        QuestionBank questionBank=course.get().getQuestionBank();
        if(questionBank==null){
            return ResponseEntity.status(404).body("No question bank for this course");

        }
        List<QuestionDto> questionDtos = questionBank.getQuestions()
                .stream()
                .map(QuestionMapper::toDTO)
                .toList();
        return ResponseEntity.ok(questionDtos);
    }
}

