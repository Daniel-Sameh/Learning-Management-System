package com.swe.lms.courseManagement.Controller;

import com.swe.lms.courseManagement.Service.CourseService;
import com.swe.lms.courseManagement.entity.Course;
import com.swe.lms.userManagement.Exception.ResourceNotFoundException;
import com.swe.lms.userManagement.entity.User;
import com.swe.lms.userManagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping("/{courseId}/enroll/{userId}")
    public ResponseEntity<String> enrollUserInCourse(
            @PathVariable Long courseId,
            @PathVariable Long userId) {
        courseService.enrollUserInCourse(courseId, userId);
        return ResponseEntity.ok("User enrolled successfully in the course");
    }

    @PreAuthorize("hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<Course> createCourse(@RequestBody Map<String, Object> courseRequest) {
        String name = (String) courseRequest.get("name");
        String code = (String) courseRequest.get("code");
        Long instructorId = Long.valueOf(courseRequest.get("instructor").toString());

        // Fetch the instructor by ID from the UserRepository
        User instructor = userRepository.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found"));

        // Create the course object and set its properties
        Course course = new Course();
        course.setName(name);
        course.setCode(code);
        course.setInstructor(instructor);  // Set the fetched instructor

        // Create and save the course
        Course createdCourse = courseService.createCourse(course);
        return ResponseEntity.ok(createdCourse);
    }

    @PreAuthorize("hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
    @PutMapping("/{courseId}/update")
    public ResponseEntity<Course> updateCourse(@PathVariable Long courseId, @RequestBody Map<String, Object> updatedCourseRequest) {
        String name = (String) updatedCourseRequest.get("name");
        String code = (String) updatedCourseRequest.get("code");
        Long instructorId = Long.valueOf(updatedCourseRequest.get("instructor").toString());


        User instructor = userRepository.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found"));


        Course updatedCourse = new Course();
        updatedCourse.setName(name);
        updatedCourse.setCode(code);
        updatedCourse.setInstructor(instructor);

        Course updated = courseService.updateCourse(courseId, updatedCourse);
        return ResponseEntity.ok(updated);
    }
}
