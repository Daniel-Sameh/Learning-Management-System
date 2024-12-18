package com.swe.lms.courseManagement.Controller;

import com.swe.lms.courseManagement.Repository.CourseRepository;
import com.swe.lms.courseManagement.Service.CourseService;
import com.swe.lms.courseManagement.entity.Course;
import com.swe.lms.courseManagement.entity.Post;
import com.swe.lms.courseManagement.Service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private CourseService courseService;

    // Endpoint to create a post
    @PostMapping("/create")
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> createPost(
            @RequestParam(value="title") String title,
            @RequestParam(value ="content") String content,
            @RequestParam(value ="courseId") Long courseId,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        try {
            // Call the service to create a post
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new RuntimeException("Course not found"));
            if (course == null) {
                return ResponseEntity.status(404).body("Course not found");
            }
            Post post = postService.createPost(title, content, courseId, file);
            courseService.notify(
                    "New Announcement: \"" + course.getName() + "\"",
                    "Title: " + title + "<br>Content: " + content,
                    course
            );

            return ResponseEntity.ok("Post created successfully with ID: " + post.getId());
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error saving file: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body("Course not found: " + e.getMessage());
        }
    }
}
