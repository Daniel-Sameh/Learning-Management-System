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
        // Configure Cloudinary
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "dpvaasmox");  // Replace with your Cloudinary cloud name
        config.put("api_key", "194844265729344");       // Replace with your Cloudinary API key
        config.put("api_secret", "FOBe4DlLK6FnNqgXMOeTRJVevLE"); // Replace with your Cloudinary API secret
        Cloudinary cloudinary = new Cloudinary(config);

        // Generate a random number to ensure uniqueness
        int randomNum = ThreadLocalRandom.current().nextInt(100000, 999999);

        // Extract original file name and append random number
        String originalFileName = file.getOriginalFilename();
        String fileNameWithRandom = randomNum + "_" + originalFileName;

        // Prepare Cloudinary upload parameters
        Map<String, Object> uploadParams = new HashMap<>();
        uploadParams.put("folder", "uploads");
        uploadParams.put("public_id", fileNameWithRandom); // Set the file name in Cloudinary

        // Create a temporary file for uploading
        File tempFile = File.createTempFile("temp", file.getOriginalFilename());
        file.transferTo(tempFile);

        // Upload to Cloudinary
        Map uploadResult;
        try {
            uploadResult = cloudinary.uploader().upload(tempFile, uploadParams);
        } finally {
            tempFile.delete();
        }

        // Return the Cloudinary URL for the uploaded file
        return (String) uploadResult.get("url");
    }
}
