package com.swe.lms.courseManagement.Service;

import com.swe.lms.courseManagement.entity.Course;
import com.swe.lms.courseManagement.entity.Post;
import com.swe.lms.courseManagement.Repository.CourseRepository;
import com.swe.lms.courseManagement.Repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CourseRepository courseRepository;

    // Method to create a post with optional media file upload
    public Post createPost(String title, String content, Long courseId, MultipartFile file) throws IOException {
        // Check if the course exists
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // Create a new post
        Post post = new Post();
        post.setTitle(title);
        post.setContent(content); // You can store the content (text) directly
        post.setLectureDate(LocalDateTime.now()); // Set the current date

        if (file != null && !file.isEmpty()) {
            // Process the file (upload to the server)
            String mediaPath = saveMediaFile(file);
            post.setMedia(mediaPath);
        }

        // Set the course for this post
        post.setCourse(course);

        // Save the post to the database
        return postRepository.save(post);
    }

    // Helper method to save the uploaded media file and return its path
    private String saveMediaFile(MultipartFile file) throws IOException {
        // Get the application's root directory (current working directory)
        String baseDir = System.getProperty("user.dir");

        // Define the relative upload directory (e.g., inside a "uploads" folder)
        String uploadDir = baseDir + "/uploads/";
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Get the original file name
        String originalFileName = file.getOriginalFilename();

        // Generate a random number and append it to the file name to ensure uniqueness
        int randomNum = ThreadLocalRandom.current().nextInt(100000, 999999); // Random 6-digit number
        String fileName = randomNum + "_" + originalFileName;  // Randomized file name
        Path filePath = Paths.get(uploadDir + fileName);

        // Save the file to the directory
        Files.write(filePath, file.getBytes());

        // Return the path to be stored in the database
        return filePath.toString();
    }
}
