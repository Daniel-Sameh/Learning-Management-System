package com.swe.lms.courseManagement.Controller;

import com.swe.lms.courseManagement.Service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/courses")
public class CourseController {
    @Autowired
    private CourseService courseService;

    @PostMapping("/{courseId}/enroll/{userId}")
    public ResponseEntity<String> enrollUserInCourse(
            @PathVariable Long courseId,
            @PathVariable Long userId) {
        courseService.enrollUserInCourse(courseId, userId);
        return ResponseEntity.ok("User enrolled successfully in the course");
    }
}
