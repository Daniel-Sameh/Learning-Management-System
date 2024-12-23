package com.swe.lms.courseManagement.Service;

import com.swe.lms.courseManagement.Repository.CourseRepository;
import com.swe.lms.courseManagement.dto.CourseDTO;
import com.swe.lms.courseManagement.dto.StudentDTO;
import com.swe.lms.courseManagement.entity.Course;
import com.swe.lms.userManagement.entity.User;
import com.swe.lms.userManagement.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import org.apache.poi.sl.usermodel.ObjectMetaData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class CourseServiceTest {
    @Mock
    private CourseRepository courseRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private CourseService courseService;

    AutoCloseable autoCloseable; //close all unwanted resources when the class finishes exec
    Course course;
    User user;
    User instructor;
    User admin;
    User student;

    private static final String SECRET_KEY = "9D0EB6B1C2E1FAD0F53A248F6C3B5E4E2F6D8G3H1I0J7K4L1M9N2O3P5Q0R7S9T1U4V2W6X0Y3Z";
    private String validToken;
    private String invalidToken;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(2022L);
        user.setUsername("testuser");

        instructor = new User();
        instructor.setId(147L);
        instructor.setUsername("Malak");

        student = new User();
        student.setId(123L);
        student.setUsername("a");

        course = new Course();
        course.setId(122L);
        course.setCode("167L");
        course.setName("logic design");
        course.setStudents(new ArrayList<>());
        course.setInstructor(instructor);

        validToken = Jwts.builder()
                .setSubject("testuser")
                .claim("role", "admin")
                .signWith(io.jsonwebtoken.SignatureAlgorithm.HS256, SECRET_KEY.getBytes())
                .compact();
        invalidToken = "aaaaa";

    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void enrollUserInCourseTest_success() {
        Mockito.when(courseRepository.save(course)).thenReturn(course); //mock fa msh bysave lel database
        Mockito.when(courseRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.of(course)); //since enna mocked el repo hanmock el methods elly fel repo bardo
        Mockito.when(userRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.of(user));
        courseService.enrollUserInCourse(course.getId(), user.getId()); //feha el injected mocks bto3na !!
        assertThat(course.getStudents().contains(user)).isTrue();
    }

    @Test
    void createCourse_success() {
        Map<String, Object> courseRequest = new HashMap<>();
        //courseDTO structure
        courseRequest.put("name", "Discrete");
        courseRequest.put("code","Dis123");
        courseRequest.put("instructor",147L);
        //dummy user
        User instructor = new User();
        instructor.setId(147L);
        instructor.setUsername("Malak");
        //duumy course
        Course course = new Course();
        course.setName("Discrete");
        course.setCode("Dis123");
        course.setInstructor(instructor);

        Mockito.when(userRepository.findById(147L)).thenReturn(Optional.of(instructor));
        Mockito.when(courseRepository.findByNameOrCode("Discrete", "Dis123")).thenReturn(null);
        Mockito.when(courseRepository.save(ArgumentMatchers.any())).thenReturn(course);

        CourseDTO cdto = courseService.createCourse(courseRequest,validToken);
        assertThat(cdto.getName()).isEqualTo("Discrete");
        assertThat(cdto.getCode()).isEqualTo("Dis123");
    }
    @Test
    void createCourse_InvalidToken_Fail() {
        Map<String, Object> courseRequest = new HashMap<>();
        courseRequest.put("name", "Discrete");
        courseRequest.put("code","Dis123");
        courseRequest.put("instructor",147L);

        Exception e = Assertions.assertThrows(IllegalArgumentException.class,()->courseService.createCourse(courseRequest,invalidToken));
        assertThat(e.getMessage()).isEqualTo("Invalid token");
    }
    @Test
    void createCourse_alreadycreated_Fail() {
        Map<String, Object> courseRequest = new HashMap<>();
        courseRequest.put("name", "Discrete");
        courseRequest.put("code","Dis123");
        courseRequest.put("instructor",147L);



        Course sameCourse = new Course();

        Mockito.when(userRepository.findById(147L)).thenReturn(Optional.of(instructor));
        Mockito.when(courseRepository.findByNameOrCode("Discrete", "Dis123")).thenReturn(sameCourse);

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class,
                () -> courseService.createCourse(courseRequest, validToken));

        Assertions.assertEquals("Course with the same name or code already exists.", exception.getMessage());
    }

    @Test
    void updateCourse_success() {
        Mockito.when(courseRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.of(course));
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", "Discrete");
        updates.put("code","Dis123");
        updates.put("instructor",77L);
        course.setInstructor(instructor);

        Mockito.when(courseRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.of(course));
        Mockito.when(courseRepository.findByNameOrCode(ArgumentMatchers.any(),ArgumentMatchers.any())).thenReturn(null);
        Mockito.when(userRepository.findByUsername(ArgumentMatchers.any())).thenReturn(Optional.of(instructor));
        Mockito.when(userRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.of(instructor));
        Mockito.when(courseRepository.save(course)).thenReturn(course);

        CourseDTO cdto = courseService.updateCourse(course.getId(),updates,validToken);
        assertThat(course.getName()).isEqualTo(updates.get("name"));
        assertThat(course.getCode()).isEqualTo(updates.get("code"));
    }

    @Test
    void updateCourse_invalidToken() {
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", "Discrete");
        updates.put("code","Dis123");
        updates.put("instructor",147L);
        course.setInstructor(instructor);

        Mockito.when(courseRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.of(course));
        Mockito.when(courseRepository.findByNameOrCode(ArgumentMatchers.any(),ArgumentMatchers.any())).thenReturn(null);
        Mockito.when(userRepository.findByUsername(ArgumentMatchers.any())).thenReturn(Optional.of(instructor));
        Mockito.when(userRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.of(instructor));
        Mockito.when(courseRepository.save(course)).thenReturn(course);

        Exception e = Assertions.assertThrows(IllegalArgumentException.class,()->courseService.updateCourse(course.getId(),updates,invalidToken)) ;
        assertThat(e.getMessage()).isEqualTo("Invalid token");
    }
    @Test
    void updateCourse_existingNameOrCode() {
        Mockito.when(courseRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.of(course));
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", "logic design");
        updates.put("code", "167L");
        updates.put("instructor", 147L);

        Course conflictingCourse = new Course();
        conflictingCourse.setId(999L); //id mo5tlf 3n el course
        conflictingCourse.setName("logic design");
        conflictingCourse.setCode("167L");

        Mockito.when(courseRepository.findByNameOrCode(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(conflictingCourse);
        Mockito.when(userRepository.findByUsername(ArgumentMatchers.any())).thenReturn(Optional.of(instructor));

        Exception e = Assertions.assertThrows(IllegalArgumentException.class, () -> courseService.updateCourse(course.getId(), updates, validToken));
        assertThat(e.getMessage()).isEqualTo("Course with the same name or code already exists.");
    }

    @Test
    void getAllCourses() {
        Course testCourse1 = new Course();
        Course testCourse2 = new Course();

        testCourse1.setName("Algebra");
        testCourse1.setId(142536L);
        testCourse1.setInstructor(user);

        testCourse2.setName("Physics");
        testCourse2.setId(142537L);
        testCourse2.setInstructor(user);

        List<Course> courseList = new ArrayList<>();
        courseList.add(testCourse1);
        courseList.add(testCourse2);

        Mockito.when(courseRepository.save(testCourse1)).thenReturn(testCourse1);
        Mockito.when(courseRepository.save(testCourse2)).thenReturn(testCourse2);
        Mockito.when(courseRepository.findAll()).thenReturn(courseList);

        List<CourseDTO> courses = courseService.getAllCourses();
        assertThat(courses.size()).isEqualTo(2);
    }

    @Test
    void getStudentsEnrolledInCourse_success() {
    Course c = new Course();

    User student2 = new User();
    student2.setId(1234L);
    student2.setUsername("b");

    User student3 = new User();
    student3.setId(1234L);
    student3.setUsername("c");

    List<User> studentList = new ArrayList<>();
    studentList.add(student);
    studentList.add(student2);
    studentList.add(student3);
    c.setStudents(studentList);

    Mockito.when(courseRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.of(c));
    List<StudentDTO> l = courseService.getStudentsEnrolledInCourse(444L);
    assertThat(l.size()).isEqualTo(3);
    }

    @Test
    void getStudentsEnrolledInCourse_courseNotFound() {
        Mockito.when(courseRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.empty());
        Exception ex = Assertions.assertThrows(IllegalArgumentException.class,()->
                courseService.getStudentsEnrolledInCourse(444L));
        assertThat(ex.getMessage()).isEqualTo("Course not found");

    }

    @Test
    void removeStudentFromCourse_success() {
        List l = new ArrayList<User>();
        l.add(student);
        course.setStudents(l);
        course.setInstructor(instructor);

        Mockito.when(courseRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.of(course));
        Mockito.when(userRepository.findByUsername(ArgumentMatchers.any())).thenReturn(Optional.of(instructor));
        Mockito.when(userRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.of(student));
        Mockito.when(courseRepository.save(course)).thenReturn(course);

        boolean isRemoved = courseService.removeStudentFromCourse(course.getId(), student.getId(),validToken);
        assertThat(isRemoved).isEqualTo(true);
        assertThat(l.size()).isEqualTo(0);
    }
    @Test
    void removeStudentFromCourse_invalidToken() {
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class,
                () -> courseService.removeStudentFromCourse(course.getId(), student.getId(),invalidToken));
        assertThat(exception.getMessage()).isEqualTo("Invalid token");
    }
    @Test
    void removeStudentFromCourse_differentInstructor() {
        User instructor2 = new User();
        instructor2.setId(1234L);
        instructor2.setUsername("namee");

        List l = new ArrayList<User>();
        l.add(student);
        course.setStudents(l);
        course.setInstructor(instructor);

        Mockito.when(courseRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.of(course));
        Mockito.when(userRepository.findByUsername(ArgumentMatchers.any())).thenReturn(Optional.of(instructor2));
        Mockito.when(userRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.of(student));
        Mockito.when(courseRepository.save(course)).thenReturn(course);

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class,
                () -> courseService.removeStudentFromCourse(course.getId(), student.getId(),validToken));
        assertThat(exception.getMessage()).isEqualTo("You are not authorized to remove student from this course.");
    }
    @Test
    void removeStudentFromCourse_studentNotFound() {
        List l = new ArrayList<User>();
        course.setStudents(l);
        course.setInstructor(instructor);

        Mockito.when(courseRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.of(course));
        Mockito.when(userRepository.findByUsername(ArgumentMatchers.any())).thenReturn(Optional.of(instructor));
        Mockito.when(userRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.empty());
        Mockito.when(courseRepository.save(course)).thenReturn(course);

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class,
                () -> courseService.removeStudentFromCourse(course.getId(), student.getId(),validToken));
        assertThat(exception.getMessage()).isEqualTo("Student not found");
    }
    @Test
    void removeStudentFromCourse_courseNorFound() {
        Mockito.when(courseRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.empty());
        Mockito.when(userRepository.findByUsername(ArgumentMatchers.any())).thenReturn(Optional.of(instructor));
        Mockito.when(userRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.of(student));
        Mockito.when(courseRepository.save(course)).thenReturn(course);

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class,
                () -> courseService.removeStudentFromCourse(course.getId(), student.getId(),validToken));
        assertThat(exception.getMessage()).isEqualTo("Course not found");
    }

    @Test
    void testNotify() {

    }

}
