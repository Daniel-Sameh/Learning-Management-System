package com.swe.lms.AssessmentManagement.Controller;

import com.swe.lms.AssessmentManagement.Repository.AssignmentRepository;
import com.swe.lms.AssessmentManagement.dto.AssignmentDto;
import com.swe.lms.courseManagement.Repository.CourseRepository;
import com.swe.lms.courseManagement.entity.Course;
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
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AssignmentControllerTest {

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

    @PersistenceContext
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        baseUrl = String.format("http://localhost:%d/api/assignments", port);
        apiUrl = String.format("http://localhost:%d/api", port);


        assignmentRepository.deleteAll();
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

            // Sign up
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
    void testCreateAssignment() {
        try {

            User instructor = userRepository.findByUsername("instructorUser")
                    .orElseThrow(() -> new RuntimeException("Instructor not found"));

            Course course = new Course();
            course.setName("Test Course");
            course.setCode("TEST101");
            course.setInstructor(instructor);
            course = courseRepository.save(course);


            System.out.println("Course ID: " + course.getId());
            System.out.println("Instructor ID: " + instructor.getId());
            System.out.println("Instructor Token: " + instructorToken);


            LocalDateTime deadline = LocalDateTime.now().plusDays(7);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
            String formattedDeadline = deadline.format(formatter);

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(instructorToken);

            String baseUrlWithPath = baseUrl + "/create/course/" + course.getId();


            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("title", "Test Assignment");
            params.add("decription", "Test Description");
            params.add("deadline", formattedDeadline);


            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrlWithPath)
                    .queryParams(params);

            String createUrl = builder.build(false).toUriString();  // false to prevent double encoding 3shan kanet 3amla error


            System.out.println("Request URL: " + createUrl);
            System.out.println("Request Headers: " + headers);

            HttpEntity<?> requestEntity = new HttpEntity<>(headers);

            //Enable detailed error handling
            RestTemplate restTemplateWithErrorHandler = new RestTemplate();
            restTemplateWithErrorHandler.setErrorHandler(new ResponseErrorHandler() {
                @Override
                public boolean hasError(ClientHttpResponse response) throws IOException {
                    return response.getStatusCode().is4xxClientError() ||
                            response.getStatusCode().is5xxServerError();
                }

                @Override
                public void handleError(ClientHttpResponse response) throws IOException {
                    String responseBody = new String(response.getBody().readAllBytes());
                    System.err.println("Error Status Code: " + response.getStatusCode());
                    System.err.println("Error Response Body: " + responseBody);
                    throw new HttpServerErrorException(response.getStatusCode(), responseBody);
                }
            });

            ResponseEntity<AssignmentDto> response = restTemplateWithErrorHandler.exchange(
                    createUrl,
                    HttpMethod.POST,
                    requestEntity,
                    AssignmentDto.class
            );

            System.out.println("Response Status: " + response.getStatusCode());
            System.out.println("Response Body: " + response.getBody());


            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            AssignmentDto createdAssignment = response.getBody();
            assertEquals("Test Assignment", createdAssignment.getTitle());
            assertEquals("Test Description", createdAssignment.getDescription());
            assertEquals(course.getId(), createdAssignment.getCourseId());
            assertEquals(instructor.getId(), createdAssignment.getInstructorId());

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

    @Test
    void testCreateAssignment_DeadlineInavlid() {
        try {
            // Create test data
            User instructor = userRepository.findByUsername("instructorUser")
                    .orElseThrow(() -> new RuntimeException("Instructor not found"));

            Course course = new Course();
            course.setName("Test Course");
            course.setCode("TEST101");
            course.setInstructor(instructor);
            course = courseRepository.save(course);

            System.out.println("Course ID: " + course.getId());
            System.out.println("Instructor ID: " + instructor.getId());
            System.out.println("Instructor Token: " + instructorToken);


            String invalidDeadline = "invalid-date-format";

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(instructorToken);

            String baseUrlWithPath = baseUrl + "/create/course/" + course.getId();


            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("title", "Test Assignment");
            params.add("decription", "Test Description");
            params.add("deadline", invalidDeadline);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrlWithPath)
                    .queryParams(params);

            String createUrl = builder.build(false).toUriString();

            System.out.println("Request URL: " + createUrl);
            System.out.println("Request Headers: " + headers);

            HttpEntity<?> requestEntity = new HttpEntity<>(headers);


            try {
                restTemplate.exchange(
                        createUrl,
                        HttpMethod.POST,
                        requestEntity,
                        AssignmentDto.class
                );

                // If we reach here, the test should fail because we expected an exception
                fail("Expected exception was not thrown");

            } catch (HttpServerErrorException e) {

                assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, e.getStatusCode());
                String responseBody = e.getResponseBodyAsString();
                assertNotNull(responseBody);
                assertTrue(responseBody.contains("error") || responseBody.contains("message"));


                System.out.println("Expected error caught:");
                System.out.println("Status code: " + e.getStatusCode());
                System.out.println("Response body: " + responseBody);
            }

        } catch (Exception e) {
            System.err.println("Test failed with unexpected exception: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Test
    void testCreateAssignment_CourseDoesNotExist() {
        try {
            Long nonExistentCourseId = 99999L;

            LocalDateTime deadline = LocalDateTime.now().plusDays(7);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
            String formattedDeadline = deadline.format(formatter);

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(instructorToken);

            String baseUrlWithPath = baseUrl + "/create/course/" + nonExistentCourseId;

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("title", "Test Assignment");
            params.add("decription", "Test Description");
            params.add("deadline", formattedDeadline);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrlWithPath)
                    .queryParams(params);

            String createUrl = builder.build(false).toUriString();

            System.out.println("Request URL: " + createUrl);
            System.out.println("Request Headers: " + headers);

            HttpEntity<?> requestEntity = new HttpEntity<>(headers);

            try {
                restTemplate.exchange(
                        createUrl,
                        HttpMethod.POST,
                        requestEntity,
                        AssignmentDto.class
                );

                fail("Expected exception was not thrown");

            } catch (HttpClientErrorException.NotFound e) {
                assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
                String responseBody = e.getResponseBodyAsString();
                assertNotNull(responseBody);
                assertTrue(responseBody.contains("Course not found"));

                // Log the error details
                System.out.println("Expected error caught successfully:");
                System.out.println("Status code: " + e.getStatusCode());
                System.out.println("Response body: " + responseBody);
            }

        } catch (Exception e) {
            if (!(e instanceof HttpClientErrorException.NotFound)) {
                System.err.println("Test failed with unexpected exception type: " + e.getClass().getName());
                System.err.println("Exception message: " + e.getMessage());
                e.printStackTrace();
                throw e;
            }
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