package com.swe.lms.courseManagement.Controller;

import com.swe.lms.courseManagement.Repository.CourseRepository;
import com.swe.lms.courseManagement.Service.CourseService;
import com.swe.lms.courseManagement.dto.PostDto;
import com.swe.lms.courseManagement.entity.Course;
import com.swe.lms.courseManagement.entity.Post;
import com.swe.lms.courseManagement.Service.PostService;
import com.swe.lms.userManagement.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
    @PostMapping("/create/{courseId}")
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> createPost(
            @RequestParam(value="title") String title,
            @RequestParam(value ="content") String content,
            @PathVariable Long courseId,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        try {
            // Call the service to create a post
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new RuntimeException("Course not found"));
            if (course == null) {
                return ResponseEntity.status(404).body("Course not found");
            }
            Post post = postService.createPost(title, content, courseId, file);
            System.out.println("Created the post....");
            courseService.notify(
                    "New Announcement: \"" + course.getName() + "\"",
                    "Title: " + title + "<br>Content: " + content,
                    course
            );
            System.out.println("Notified the students....");

            return ResponseEntity.ok("Post created successfully with ID: " + post.getId());
            
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error saving file: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body("Course not found: " + e.getMessage());
        }
    }

    @GetMapping("/get/{id}/course/{courseId}")
    public ResponseEntity<PostDto> getPost(@AuthenticationPrincipal User user, @PathVariable Long id, @PathVariable Long courseId) {
        Optional<Post> post = postService.getPostById(id);
        if (post.isEmpty()) {
            return ResponseEntity.status(404).body(null);
        }
        Course course = post.get().getCourse();
        if (course.getId()!=courseId) {
            return ResponseEntity.status(400).body(null);
        }
        if (user.getRole().equals("STUDENT") && !courseService.isEnrolled(user, course)) {
            return ResponseEntity.status(403).body(null);
        }
        PostDto postDTO = new PostDto(post.get().getId(), post.get().getTitle(), post.get().getContent());
        return ResponseEntity.ok(postDTO);
    }

    @GetMapping("/get/course/{courseId}")
    public ResponseEntity<List<PostDto>> getAllPosts(@AuthenticationPrincipal User user, @PathVariable Long courseId) {
        List<Post> posts = postService.getAllPostsByCourseId(courseId);
        if (posts.isEmpty()) {
            return ResponseEntity.status(404).body(null);
        }
        Course course = posts.get(0).getCourse();
        if (course.getId() != courseId) {
            return ResponseEntity.status(400).body(null);
        }
        if (user.getRole().toString().equals("STUDENT") && !courseService.isEnrolled(user, course)) {
            return ResponseEntity.status(403).body(null);
        }
        List<PostDto> postDTOs = posts.stream()
                .map(post -> new PostDto(post.getId(), post.getTitle(), post.getContent()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(postDTOs);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> deletePost(@AuthenticationPrincipal User user, @PathVariable Long id) {
        if (user.getRole().toString().equals("INSTRUCTOR")) {
            System.out.println("YES I am instructor deleting post...");
            Optional<Post> post = postService.getPostById(id);
            if (post.isEmpty()) {
                return ResponseEntity.status(404).body("Post not found");
            }
            Course course = post.get().getCourse();
            System.out.println("Course Inst. id: "+course.getInstructor().getId());
            System.out.println("User id: "+user.getId());
            if (course.getInstructor().getId() != user.getId()) {
                return ResponseEntity.status(403).body("You are not authorized to delete this post");
            }
        }
        System.out.println("User role: "+user.getRole());
        try {
            Optional<Post> post = postService.getPostById(id);
            if (post.isEmpty()) {
                return ResponseEntity.status(404).body("Post not found");
            }
            postService.deletePost(id);
            return ResponseEntity.ok("Post deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body("Post not found: " + e.getMessage());
        }
    }
    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> updatepost(@PathVariable Long id, @RequestHeader("Authorization")String authorizationHeader,@RequestBody Map<String, Object> request) {
        try {
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                throw new IllegalArgumentException("Authorization header is missing or invalid");
            }
            String token = authorizationHeader.substring(7);
            Post post = postService.updatepost(request,id,token);
            System.out.println("Updated the post....");
            return ResponseEntity.ok("Post Updated successfully with ID: " + post.getId());
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body("Course not found: " + e.getMessage());
        }
    }

}
