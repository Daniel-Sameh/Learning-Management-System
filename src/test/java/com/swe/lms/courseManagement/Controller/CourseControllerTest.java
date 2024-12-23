package com.swe.lms.courseManagement.Controller;

import com.swe.lms.courseManagement.Repository.CourseRepository;
import com.swe.lms.courseManagement.entity.Course;
import com.swe.lms.security.dao.request.SignUpRequest;
import com.swe.lms.security.dao.request.SigninRequest;
import com.swe.lms.security.dao.response.JwtAuthenticationResponse;
import com.swe.lms.userManagement.entity.Role;
import com.swe.lms.userManagement.entity.User;
import com.swe.lms.userManagement.repository.UserRepository;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.client.RestTemplate;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CourseControllerTest {

    private String studentToken;

    @LocalServerPort
    private int port;

    private String baseUrl;
    private String apiUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        baseUrl = String.format("http://localhost:%d/api/courses", port);
        apiUrl = String.format("http://localhost:%d/api", port);

        // Clean up the database
        courseRepository.deleteAll();
        userRepository.deleteAll();


        createStudentAndSignIn();
    }

    private void createStudentAndSignIn() {
        try {
            SignUpRequest signUpRequest = new SignUpRequest();
            signUpRequest.setUsername("studentUser");
            signUpRequest.setPassword("studentPassword");
            signUpRequest.setEmail("student@example.com");


            String signupUrl = apiUrl + "/signup";
            HttpHeaders signupHeaders = new HttpHeaders();
            signupHeaders.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<SignUpRequest> signupEntity = new HttpEntity<>(signUpRequest, signupHeaders);

            ResponseEntity<JwtAuthenticationResponse> signupResponse = restTemplate.exchange(
                    signupUrl,
                    HttpMethod.POST,
                    signupEntity,
                    JwtAuthenticationResponse.class
            );


            User student = userRepository.findByUsername("studentUser")
                    .orElseThrow(() -> new RuntimeException("Student not found"));
            student.setRole(Role.STUDENT);
            userRepository.saveAndFlush(student);

            // Sign in
            SigninRequest signinRequest = new SigninRequest();
            signinRequest.setUsername("studentUser");
            signinRequest.setPassword("studentPassword");

            String signinUrl = apiUrl + "/signin";
            HttpHeaders signinHeaders = new HttpHeaders();
            signinHeaders.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<SigninRequest> signinEntity = new HttpEntity<>(signinRequest, signinHeaders);

            ResponseEntity<Map<String, Object>> signinResponse = restTemplate.exchange(
                    signinUrl,
                    HttpMethod.POST,
                    signinEntity,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            Map<String, Object> responseBody = signinResponse.getBody();
            if (responseBody != null && responseBody.containsKey("token")) {
                this.studentToken = (String) responseBody.get("token");
                System.out.println("Token received: " + this.studentToken);
            }

        } catch (Exception e) {
            System.err.println("Error during student setup: " + e.getMessage());
            throw new RuntimeException("Student setup failed", e);
        }
    }

    @Test
    void testEnrollUserInCourse() {

        User instructor = new User();
        instructor.setUsername("instructor");
        instructor.setEmail("instructor@test.com");
        instructor.setPassword("password");
        instructor.setRole(Role.INSTRUCTOR);
        instructor = userRepository.save(instructor);


        User student = userRepository.findByUsername("studentUser")
                .orElseThrow(() -> new RuntimeException("Student not found"));


        Course course = new Course();
        course.setName("Test Course");
        course.setCode("TEST101");
        course.setInstructor(instructor);
        course.setStudents(new ArrayList<>());
        course = courseRepository.save(course);


        System.out.println("Student ID: " + student.getId());
        System.out.println("Student Role: " + student.getRole());
        System.out.println("Course ID: " + course.getId());
        System.out.println("Token: " + studentToken);


        String enrollUrl = String.format("%s/%d/enroll/%d", baseUrl, course.getId(), student.getId());
        System.out.println("Enrollment URL: " + enrollUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + studentToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                enrollUrl,
                HttpMethod.POST,
                requestEntity,
                String.class
        );


        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertEquals("User enrolled successfully in the course", response.getBody());


        boolean isEnrolled = entityManager.createQuery(
                        "SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Course c " +
                                "JOIN c.students s WHERE c.id = :courseId AND s.id = :studentId",
                        Boolean.class)
                .setParameter("courseId", course.getId())
                .setParameter("studentId", student.getId())
                .getSingleResult();
//jwt bta5od el role mn el userdetails directly, fa b3ml student a set el role bstudent
// query 3sahn msh 3aref a set el role fel signup req fa ba7ot fel repo w a check enno student mawgood
        Assertions.assertTrue(isEnrolled, "Student should be enrolled in the course");
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public JavaMailSender javaMailSender() {
            return new JavaMailSenderImpl();
        }

        @Bean
        public ServletWebServerFactory webServerFactory() {
            return new TomcatServletWebServerFactory();
        }

        @Bean
        public RestTemplate restTemplate() {
            return new RestTemplate();
        }
    }
}