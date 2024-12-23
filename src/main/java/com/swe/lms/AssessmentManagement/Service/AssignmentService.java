package com.swe.lms.AssessmentManagement.Service;

import com.cloudinary.Cloudinary;
import io.github.cdimascio.dotenv.Dotenv;
import com.swe.lms.AssessmentManagement.Repository.AssignmentRepository;
import com.swe.lms.AssessmentManagement.Repository.AssignmentSubmissionRepository;
import com.swe.lms.AssessmentManagement.entity.Assignment;
import com.swe.lms.AssessmentManagement.entity.AssignmentSubmission;
import com.swe.lms.courseManagement.Service.CourseService;
import com.swe.lms.courseManagement.entity.Course;
import com.swe.lms.exception.ResourceNotFoundException;
import com.swe.lms.userManagement.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssignmentService {
    static Dotenv dotenv = Dotenv.load();
    @Autowired
    private final AssignmentRepository assignmentRepository;

    @Autowired
    private final AssignmentSubmissionRepository assignmentSubmissionRepository;

    @Autowired
    private final CourseService courseService;

    public Assignment createAssignment(Long courseId, String title, String description, String deadline, User instructor) {
        System.out.println("I am in create assignment...");
        Assignment assignment = new Assignment();
        System.out.println("After constructor....");
        assignment.setTitle(title);
        assignment.setDescription(description);
        System.out.println("Deadline from service: "+ deadline);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
        System.out.println("IN BETWEEN...");
        LocalDateTime deadLine = LocalDateTime.parse(deadline, formatter);
        System.out.println("Deadline from service: "+ deadLine);
        assignment.setDeadline(deadLine);
        Optional<Course> course = courseService.getCourseById(courseId);
        if (course.isEmpty()) {
            throw new ResourceNotFoundException("Course not found.");
        }
        assignment.setCourse(course.get());
        assignment.setInstructor(instructor);
        System.out.println("I will save in the DB now...");
        assignmentRepository.save(assignment);
        return assignment;
    }
    public String submitAssignment(Long assignmentId, MultipartFile file, User student) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found."));
        LocalDateTime now = LocalDateTime.now();
        if (assignment.getDeadline().isBefore(now)) {
            return "Assignment submission failed. Deadline has passed.";
        }
        try {
            String url = this.saveMediaFile(file);
            AssignmentSubmission assignmentSubmission = new AssignmentSubmission();
            assignmentSubmission.setAssignment(assignment);
            assignmentSubmission.setMedia(url);
            assignmentSubmission.setStudent(student);
            assignmentSubmission.setStatus("submitted");
            assignmentSubmission.setSubmissionTime(LocalDateTime.now());
            assignmentSubmissionRepository.save(assignmentSubmission);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return "Assignment submitted successfully.";

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

    public Optional<Assignment> getAssignmentById(Long id) {
        return assignmentRepository.findById(id);
    }

    public List<AssignmentSubmission> getSubmissions(Long assignmentId) {
        List<AssignmentSubmission> submissions = assignmentSubmissionRepository.findAllByAssignment_Id(assignmentId);
        System.out.println("WE got from the DB the submissions...");
        if (submissions.isEmpty()) {
            throw new ResourceNotFoundException("No submissions found for this assignment.");
        }
        return submissions;
    }

    public AssignmentSubmission getSubmissionById(Long submissionId) {
        return assignmentSubmissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found."));
    }

    public String gradeAssignment(AssignmentSubmission assignmentSubmission, float grade, User instructor) {
        assignmentSubmission.setGrade(grade);
        assignmentSubmission.setStatus("Graded");
        assignmentSubmissionRepository.save(assignmentSubmission);
        return "Assignment graded successfully.";
    }

    public List<List<AssignmentSubmission>> getSubmissionsByCourseId(Long courseId) {
        Optional<List<Assignment>> assignments = assignmentRepository.findAllByCourse_Id(courseId);
        if (assignments.isEmpty()) {
            throw new ResourceNotFoundException("No assignments found for this course.");
        }
        List<List<AssignmentSubmission>> submissions = assignments.get().stream()
                .map(assignment -> assignmentSubmissionRepository.findAllByAssignment_Id(assignment.getId()))
                .collect(Collectors.toList());
        return submissions;
    }
    public void notify(String subject, String body, Assignment assignment){
        List<User> students=assignment.getCourse().getStudents();
        notificationService.sendNotification(students, subject, body);
    }

}
