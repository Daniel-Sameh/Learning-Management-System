package com.swe.lms.courseManagement.Service;

import com.swe.lms.courseManagement.Repository.LectureRepository;
import com.swe.lms.courseManagement.Repository.PostRepository;
import com.swe.lms.courseManagement.dto.CourseDTO;
import com.swe.lms.courseManagement.dto.StudentDTO;
import com.swe.lms.courseManagement.entity.Course;
import com.swe.lms.courseManagement.Repository.CourseRepository;
import com.swe.lms.courseManagement.entity.Lecture;
import com.swe.lms.courseManagement.entity.Post;
import com.swe.lms.notification.service.NotificationService;
import com.swe.lms.userManagement.Exception.ResourceNotFoundException;
import com.swe.lms.userManagement.entity.User;
import com.swe.lms.userManagement.repository.UserRepository;
import io.github.cdimascio.dotenv.Dotenv;
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
    static Dotenv dotenv = Dotenv.load();

    private static final String SECRET_KEY = dotenv.get("SECRET_KEY");

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private LectureRepository lectureRepository;

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


    public CourseDTO updateCourse(Long courseId,Map<String, Object> updatedCourseDetails,String token) {
        Course existingCourse = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        String name = (String) updatedCourseDetails.get("name");
        String code = (String) updatedCourseDetails.get("code");
        Course Isused = courseRepository.findByNameOrCode(name, code);
        //2 query code mo5tlf wa name mo5tlf fix lw fe wa2t
        if (Isused != null && !Isused.getId().equals(existingCourse.getId())) {
            throw new IllegalArgumentException("Course with the same name or code already exists.");
        }
        String role;
        String username;

        try {
            Claims claims = Jwts.parser().setSigningKey(SECRET_KEY.getBytes()).parseClaimsJws(token).getBody();
            role = (String) claims.get("role");
            username = (String) claims.getSubject();
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid token", e);
        }
        User instructor=userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if ("admin".equalsIgnoreCase(role)) {
            Long instructorId = Long.valueOf(updatedCourseDetails.get("instructor").toString());
             instructor = userRepository.findById(instructorId)
                    .orElseThrow(() -> new IllegalArgumentException("Instructor not found"));
            existingCourse.setInstructor(instructor);
        }
        else {
            if (!existingCourse.getInstructor().getId().equals(instructor.getId())) {
                throw new IllegalArgumentException("You are not authorized to update this course.");
            }
        }
        existingCourse.setName(name);
        existingCourse.setCode(code);

        courseRepository.save(existingCourse);
        return new CourseDTO(existingCourse.getName(), existingCourse.getCode(), instructor.getUsername());
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
    public boolean removeStudentFromCourse(Long courseId, Long studentId, String token) {

        String username;
        String role;
        try {
            Claims claims = Jwts.parser().setSigningKey(SECRET_KEY.getBytes()).parseClaimsJws(token).getBody();
            username = (String)claims.getSubject();
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid token", e);
        }
        User instructor=userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));
        if(instructor.getId()!=course.getInstructor().getId()){
            throw new IllegalArgumentException("You are not authorized to remove student from this course.");
        }
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
    public boolean isEnrolled(User user, Course course) {
        return course.getStudents().contains(user);
    }
    public void deleteCourse(String token,Long courseId) {
        String username;
        String role;
        try {
            Claims claims = Jwts.parser().setSigningKey(SECRET_KEY.getBytes()).parseClaimsJws(token).getBody();
            username = claims.getSubject();
            role = (String) claims.get("role");
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid token", e);
        }
        Course course=courseRepository.findById(courseId)
                .orElseThrow(()-> new RuntimeException("Course not found"));
        if (!"admin".equalsIgnoreCase(role) && !course.getInstructor().getUsername().equals(username)) {
            throw new RuntimeException("Unauthorized: Only the course instructor can delete this course");
        }
        List<Post> posts = postRepository.findByCourseId(courseId);
        postRepository.deleteAll(posts);
        List<Lecture> lectures = lectureRepository.findByCourseId(courseId);
        lectureRepository.deleteAll(lectures);
        courseRepository.delete(course);

    }


    public Optional<Course> getCourseById(Long courseId) {
        return courseRepository.findById(courseId);
    }

    public List<User> getStudentsByCourseId(Long courseId) {
        return courseRepository.findById(courseId)
                .map(Course::getStudents)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
    }

}
