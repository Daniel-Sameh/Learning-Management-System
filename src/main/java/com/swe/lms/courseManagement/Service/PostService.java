package com.swe.lms.courseManagement.Service;

import com.cloudinary.Cloudinary;
import com.swe.lms.courseManagement.entity.Course;
import com.swe.lms.courseManagement.entity.Post;
import com.swe.lms.courseManagement.Repository.CourseRepository;
import com.swe.lms.courseManagement.Repository.PostRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import io.github.cdimascio.dotenv.Dotenv;

@Service
public class PostService {
    static Dotenv dotenv = Dotenv.load();

    private static final String SECRET_KEY = dotenv.get("SECRET_KEY");
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CourseRepository courseRepository;

    public Post createPost(String title, String content, Long courseId, MultipartFile file) throws IOException {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // Create a new post
        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);  
        post.setLectureDate(LocalDateTime.now());  

        if (file != null && !file.isEmpty()) {
            String mediaPath = saveMediaFile(file);
            post.setMedia(mediaPath);
        }

 
        post.setCourse(course);

      
        return postRepository.save(post);
    }

   
    private String saveMediaFile(MultipartFile file) throws IOException {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name",dotenv.get("CLOUD_NAME") );
        config.put("api_key", dotenv.get("CLOUD_KEY"));
        config.put("api_secret", dotenv.get("CLOUD_SECRET"));
        Cloudinary cloudinary = new Cloudinary(config);

        int randomNum = ThreadLocalRandom.current().nextInt(100000, 999999);
        String originalFileName = file.getOriginalFilename();

        String fileNameWithRandom = randomNum + "_" + originalFileName.substring(0, originalFileName.lastIndexOf("."));

        Map<String, Object> uploadParams = new HashMap<>();
        uploadParams.put("folder", "uploads");
        uploadParams.put("public_id", fileNameWithRandom);

        String contentType = file.getContentType();
        if (contentType != null && contentType.startsWith("image/")) {
            uploadParams.put("resource_type", "image");
        } else if (contentType != null && contentType.startsWith("video/")) {
            uploadParams.put("resource_type", "video");
        } else if (contentType != null && contentType.startsWith("application/")) {
            uploadParams.put("resource_type", "raw");
        } else {
            throw new RuntimeException("Unsupported file type: " + contentType);
        }

        File tempFile = File.createTempFile("temp", originalFileName.substring(originalFileName.lastIndexOf(".")));
        file.transferTo(tempFile);

        Map uploadResult;
        try {
            uploadResult = cloudinary.uploader().upload(tempFile, uploadParams);
        } catch (Exception e) {
            throw new RuntimeException("Error uploading file to Cloudinary: " + e.getMessage(), e);
        } finally {
            tempFile.delete();
        }

        return (String) uploadResult.get("url");
    }

    public Optional<Post> getPostById(Long id) {
        return postRepository.findById(id);
    }

    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }
    public List<Post> getAllPostsByCourseId(Long courseId) {
        return postRepository.findByCourseId(courseId);
    }
    public Post updatepost(Map<String, Object> request,Long postId,String token){
        String username;
        try {
            Claims claims = Jwts.parser().setSigningKey(SECRET_KEY.getBytes()).parseClaimsJws(token).getBody();
            username = claims.getSubject();
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid token", e);
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        Course course=courseRepository.findById(post.getId())
                .orElseThrow(()-> new RuntimeException("Course not found"));

        if (!course.getInstructor().getUsername().equals(username)) {
            throw new RuntimeException("Unauthorized: Only the course instructor can modify this post");
        }
        if (request.containsKey("title")) {
            post.setTitle((String) request.get("title"));
        }

        if (request.containsKey("content")) {
            post.setContent((String) request.get("content"));
        }
        return postRepository.save(post);
    }
}
