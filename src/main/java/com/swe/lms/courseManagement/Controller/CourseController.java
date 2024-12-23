package com.swe.lms.courseManagement.Controller;

import com.swe.lms.courseManagement.Service.CourseService;
import com.swe.lms.courseManagement.dto.CourseDTO;
import com.swe.lms.courseManagement.dto.StudentDTO;
import com.swe.lms.courseManagement.entity.Course;
import com.swe.lms.userManagement.Exception.ResourceNotFoundException;
import com.swe.lms.userManagement.entity.User;
import com.swe.lms.userManagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;
    private final UserRepository userRepository;

    // Constructor injection
    @Autowired
    public CourseController(CourseService courseService, UserRepository userRepository) {
        this.courseService = courseService;
        this.userRepository = userRepository;
    }
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @PostMapping("/{courseId}/enroll")
    public ResponseEntity<String> enrollUserInCourse(
            @AuthenticationPrincipal User user,
            @PathVariable Long courseId) {
        Long userId = user.getId();
        courseService.enrollUserInCourse(courseId, userId);
        return ResponseEntity.ok("User enrolled successfully in the course");
    }

   @PreAuthorize("hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<CourseDTO> createCourse(@RequestBody Map<String, Object> courseRequest, @RequestHeader("Authorization")String authorizationHeader) {

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization header is missing or invalid");
        }
        String token = authorizationHeader.substring(7);

        CourseDTO  createdCourse = courseService.createCourse(courseRequest,token);
        return ResponseEntity.ok(createdCourse);
    }
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
    @PutMapping("/{courseId}/update")
    public ResponseEntity<CourseDTO> updateCourse(@PathVariable Long courseId, @RequestBody Map<String, Object> updatedCourseRequest, @RequestHeader("Authorization")String authorizationHeader) {

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization header is missing or invalid");
        }
        String token = authorizationHeader.substring(7);

        CourseDTO updated = courseService.updateCourse(courseId, updatedCourseRequest,token);
        return ResponseEntity.ok(updated);
    }
    @GetMapping("/view")
    public ResponseEntity<List<CourseDTO>> viewAllCourses() {
        // Fetch all courses from the service
        List<CourseDTO> allCourses = courseService.getAllCourses();

        // Return the list of courses
        return ResponseEntity.ok(allCourses);
    }
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
    @GetMapping("/{courseId}/students")
    public ResponseEntity<List<StudentDTO>> getStudentsEnrolledInCourse(@PathVariable Long courseId) {
        List<StudentDTO> students = courseService.getStudentsEnrolledInCourse(courseId);
        return ResponseEntity.ok(students);
    }

    @PreAuthorize("hasRole('ROLE_INSTRUCTOR')")
    @DeleteMapping("/{courseId}/remove/{studentId}")
    public ResponseEntity<String> removestudent(@PathVariable Long courseId, @PathVariable Long studentId,@RequestHeader("Authorization")String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        boolean isRemoved = courseService.removeStudentFromCourse(courseId, studentId,token);
        if (isRemoved) {
            return ResponseEntity.ok("Student removed from course successfully");
        } else {
            return ResponseEntity.status(404).body("Course or Student not found");
        }
    }

    @PreAuthorize("hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{courseId}/delete")
    public ResponseEntity<String> deletecourse(@PathVariable Long courseId,@RequestHeader("Authorization")String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization header is missing or invalid");
        }
        String token = authorizationHeader.substring(7);

        courseService.deleteCourse(token,courseId);

        return ResponseEntity.ok("Course deleted successfully");

    }
}

