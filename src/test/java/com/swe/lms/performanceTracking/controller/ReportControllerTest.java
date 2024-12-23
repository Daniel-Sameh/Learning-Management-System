package com.swe.lms.performanceTracking.controller;

import com.swe.lms.AssessmentManagement.Repository.AssignmentRepository;
import com.swe.lms.AssessmentManagement.Repository.AssignmentSubmissionRepository;
import com.swe.lms.AssessmentManagement.entity.Assignment;
import com.swe.lms.AssessmentManagement.entity.AssignmentSubmission;
import com.swe.lms.courseManagement.Repository.CourseRepository;
import com.swe.lms.courseManagement.Repository.LectureRepository;
import com.swe.lms.courseManagement.entity.Course;
import com.swe.lms.courseManagement.entity.Lecture;
import com.swe.lms.security.dao.request.SignUpRequest;
import com.swe.lms.security.dao.request.SigninRequest;
import com.swe.lms.userManagement.entity.Role;
import com.swe.lms.userManagement.entity.User;
import com.swe.lms.userManagement.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ReportControllerTest {

    private String instructorToken;

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

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private AssignmentSubmissionRepository assignmentSubmissionRepository;

    @Autowired
    private LectureRepository lectureRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        baseUrl = String.format("http://localhost:%d/api/reports", port);
        apiUrl = String.format("http://localhost:%d/api", port);


        assignmentSubmissionRepository.deleteAll();
        assignmentRepository.deleteAll();
        lectureRepository.deleteAll();
        courseRepository.deleteAll();
        userRepository.deleteAll();


        createInstructorAndSignIn();
    }

    private void createInstructorAndSignIn() {
        try {

            SignUpRequest signUpRequest = new SignUpRequest();
            signUpRequest.setUsername("instructorUser");
            signUpRequest.setPassword("instructorPassword");
            signUpRequest.setEmail("instructor@example.com");


            String signupUrl = apiUrl + "/signup";
            HttpHeaders signupHeaders = new HttpHeaders();
            signupHeaders.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<SignUpRequest> signupEntity = new HttpEntity<>(signUpRequest, signupHeaders);

            System.out.println("Attempting signup at URL: " + signupUrl);

            restTemplate.exchange(
                    signupUrl,
                    HttpMethod.POST,
                    signupEntity,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );


            User instructor = userRepository.findByUsername("instructorUser")
                    .orElseThrow(() -> new RuntimeException("Instructor not found"));
            instructor.setRole(Role.INSTRUCTOR);
            userRepository.saveAndFlush(instructor);

            System.out.println("Instructor role updated successfully");


            SigninRequest signinRequest = new SigninRequest();
            signinRequest.setUsername("instructorUser");
            signinRequest.setPassword("instructorPassword");

            String signinUrl = apiUrl + "/signin";
            HttpHeaders signinHeaders = new HttpHeaders();
            signinHeaders.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<SigninRequest> signinEntity = new HttpEntity<>(signinRequest, signinHeaders);

            System.out.println("Attempting signin at URL: " + signinUrl);

            ResponseEntity<Map<String, Object>> signinResponse = restTemplate.exchange(
                    signinUrl,
                    HttpMethod.POST,
                    signinEntity,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            Map<String, Object> responseBody = signinResponse.getBody();
            if (responseBody != null && responseBody.containsKey("token")) {
                this.instructorToken = (String) responseBody.get("token");
                System.out.println("Instructor token received: " + this.instructorToken);
            } else {
                throw new RuntimeException("No token received in signin response");
            }

        } catch (Exception e) {
            System.err.println("Error during instructor setup: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Instructor setup failed", e);
        }
    }

    @Test
    void testGenerateExcelReport() {
        try {

            User instructor = userRepository.findByUsername("instructorUser")
                    .orElseThrow(() -> new RuntimeException("Instructor not found"));


            Course course = new Course();
            course.setName("Test Course");
            course.setCode("TEST101");
            course.setInstructor(instructor);
            course = courseRepository.save(course);

            System.out.println("Course created with ID: " + course.getId());


            User student = new User();
            student.setUsername("testStudent");
            student.setEmail("student@test.com");
            student.setPassword("password");
            student.setRole(Role.STUDENT);
            student = userRepository.save(student);


            if (course.getStudents() == null) {
                course.setStudents(new ArrayList<>());
            }
            course.getStudents().add(student);
            courseRepository.save(course);

            System.out.println("Student created with ID: " + student.getId());

            Assignment assignment = new Assignment();
            assignment.setTitle("Test Assignment");
            assignment.setDescription("Test Description");
            assignment.setDeadline(LocalDateTime.now().plusDays(7));
            assignment.setCourse(course);
            assignment.setInstructor(instructor);
            assignment = assignmentRepository.save(assignment);

            System.out.println("Assignment created with ID: " + assignment.getId());


            AssignmentSubmission submission = new AssignmentSubmission();
            submission.setAssignment(assignment);
            submission.setStudent(student);
            submission.setGrade(85.0f);
            submission.setStatus("submitted");
            submission.setSubmissionTime(LocalDateTime.now());
            submission.setMedia("test-media-url");
            assignmentSubmissionRepository.save(submission);


            Lecture lecture = new Lecture();
            lecture.setName("Test Lecture");
            lecture.setCourse(course);
            lecture.setDate(LocalDateTime.now());
            lecture.setAttendanceList(new ArrayList<>());
            lecture.getAttendanceList().add(student);
            lectureRepository.save(lecture);

            System.out.println("Lecture created");


            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(instructorToken);
            headers.setContentType(MediaType.APPLICATION_JSON);


            String requestUrl = baseUrl + "/course/" + course.getId() + "/excel";
            System.out.println("Request URL: " + requestUrl);
            System.out.println("Request Headers: " + headers);

            HttpEntity<?> requestEntity = new HttpEntity<>(headers);

            ResponseEntity<byte[]> response = restTemplate.exchange(
                    requestUrl,
                    HttpMethod.GET,
                    requestEntity,
                    byte[].class
            );

            System.out.println("Response Status: " + response.getStatusCode());
            System.out.println("Response Content Type: " + response.getHeaders().getContentType());
            System.out.println("Response Content Length: " + response.getHeaders().getContentLength());

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertTrue(response.getBody().length > 0);

            HttpHeaders responseHeaders = response.getHeaders();
            assertEquals(MediaType.APPLICATION_OCTET_STREAM, responseHeaders.getContentType());
            assertTrue(responseHeaders.getContentDisposition().toString().contains("performance_report.xlsx"));

        } catch (Exception e) {
            System.err.println("Test failed with exception: " + e.getMessage());
            if (e instanceof HttpServerErrorException) {
                HttpServerErrorException serverError = (HttpServerErrorException) e;
                System.err.println("Server Error Response Body: " + serverError.getResponseBodyAsString());
            }
            e.printStackTrace();
            throw e;
        }
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