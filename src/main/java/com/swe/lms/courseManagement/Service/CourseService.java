package com.swe.lms.courseManagement.Service;

import com.swe.lms.courseManagement.dto.CourseDTO;
import com.swe.lms.courseManagement.dto.StudentDTO;
import com.swe.lms.courseManagement.entity.Course;
import com.swe.lms.courseManagement.Repository.CourseRepository;
import com.swe.lms.notification.service.NotificationService;
import com.swe.lms.userManagement.Exception.ResourceNotFoundException;
import com.swe.lms.userManagement.entity.User;
import com.swe.lms.userManagement.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CourseService {
    private static final String SECRET_KEY = "9D0EB6B1C2E1FAD0F53A248F6C3B5E4E2F6D8G3H1I0J7K4L1M9N2O3P5Q0R7S9T1U4V2W6X0Y3Z";

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    public void enrollUserInCourse(Long courseId, Long userId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!course.getStudents().contains(user)) {
            course.getStudents().add(user);
            courseRepository.save(course);
        } else {
            throw new IllegalStateException("User is already enrolled in this course");
        }
    }

    public CourseDTO  createCourse(Map<String, Object> courseRequest, String token) {
        String username;
        String role;
        try {
            Claims claims = Jwts.parser().setSigningKey(SECRET_KEY.getBytes()).parseClaimsJws(token).getBody();
            username = claims.getSubject();
            role = (String) claims.get("role");
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid token", e);
        }

        User instructor;
        Long instructorId;
        if ("admin".equalsIgnoreCase(role)) {
            instructorId = Long.valueOf(courseRequest.get("instructor").toString());
            instructor = userRepository.findById(instructorId)
                    .orElseThrow(() -> new IllegalArgumentException("Instructor not found"));
        } else {
            instructor = userRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
        }
        String name = (String) courseRequest.get("name");
        String code = (String) courseRequest.get("code");
        Course course = new Course();
        course.setName(name);
        course.setCode(code);
        course.setInstructor(instructor);
        Course existingCourse = courseRepository.findByNameOrCode(name, code);
        if (existingCourse != null) {
            throw new IllegalArgumentException("Course with the same name or code already exists.");
        }
        Course savedCourse = courseRepository.save(course);
        return new CourseDTO(savedCourse.getName(), savedCourse.getCode(), instructor.getUsername());
    }

    public Course updateCourse(Long courseId, Course updatedCourseDetails) {
        Course existingCourse = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        existingCourse.setName(updatedCourseDetails.getName());
        existingCourse.setCode(updatedCourseDetails.getCode());

        if (updatedCourseDetails.getInstructor() != null) {
            User instructor = userRepository.findById(updatedCourseDetails.getInstructor().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Instructor not found"));
            existingCourse.setInstructor(instructor);
        }

        return courseRepository.save(existingCourse);
    }
    public List<CourseDTO> getAllCourses() {
        // Fetch all courses from the repository
        List<Course> allCourses = courseRepository.findAll();

        List<CourseDTO> courseDTOs = allCourses.stream()
                .map(course -> new CourseDTO(
                        course.getName(),
                        course.getCode(),
                        course.getInstructor().getUsername()
                ))
                .collect(Collectors.toList());

        return courseDTOs;
    }
    public List<StudentDTO> getStudentsEnrolledInCourse(Long courseId) {
        // Fetch the course by its id
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        // Map the students to StudentDTOs
        return course.getStudents().stream()
                .map(student -> new StudentDTO(student.getId(), student.getUsername()))
                .collect(Collectors.toList());
    }
    public boolean removeStudentFromCourse(Long courseId, Long studentId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        if (course.getStudents().contains(student)) {
            course.getStudents().remove(student);
            courseRepository.save(course);  // Save the updated course
            return true;  // Successfully removed
        }

        return false;  // Student was not enrolled in this course
    }
    public void notify(String subject, String body, Course course){
        List<User> students = course.getStudents();
        notificationService.sendNotification(students, subject, body);
    }
    public Optional<Course> findById(Long courseId) {
        return courseRepository.findById(courseId);
    }
}
