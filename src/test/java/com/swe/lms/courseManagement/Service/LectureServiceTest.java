package com.swe.lms.courseManagement.Service;
import com.swe.lms.courseManagement.Repository.CourseRepository;
import com.swe.lms.courseManagement.Repository.LectureRepository;
import com.swe.lms.courseManagement.Service.LectureService;
import com.swe.lms.courseManagement.entity.Course;
import com.swe.lms.courseManagement.entity.Lecture;
import com.swe.lms.userManagement.entity.User;
import com.swe.lms.userManagement.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.*;

public class LectureServiceTest {

    @InjectMocks
    private LectureService lectureService;

    @Mock
    private LectureRepository lectureRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private UserRepository userRepository;

    private Lecture lecture;
    private Course course;
    private User user;
    private static final String SECRET_KEY = "9D0EB6B1C2E1FAD0F53A248F6C3B5E4E2F6D8G3H1I0J7K4L1M9N2O3P5Q0R7S9T1U4V2W6X0Y3Z";
    private String validToken;
    private String invalidToken;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize test data
        lecture = new Lecture();
        lecture.setId(1L);
        lecture.setName("Test Lecture");
        lecture.setRunning(false);

        course = new Course();
        course.setId(1L);
        course.setName("Test Course");

        user = new User();
        user.setId(1L);
        user.setUsername("testUser");

        validToken = Jwts.builder()
                .setSubject("testuser")
                .claim("role", "admin")
                .signWith(io.jsonwebtoken.SignatureAlgorithm.HS256, SECRET_KEY.getBytes())
                .compact();
        invalidToken = "aaaaa";
    }

    @Test
    void startLecture_Success() {
        lecture.setRunning(false);
        when(lectureRepository.findById(1L)).thenReturn(Optional.of(lecture));
        when(lectureRepository.save(any(Lecture.class))).thenReturn(lecture);

        String result = lectureService.startLecture(1L);

        assertNotNull(result);
        assertTrue(lecture.isRunning());
        assertEquals(6, result.length()); // OTP should be 6 digits
        verify(lectureRepository).save(lecture);
    }

    @Test
    void startLecture_AlreadyRunning() {
        lecture.setRunning(true);
        when(lectureRepository.findById(1L)).thenReturn(Optional.of(lecture));

        String result = lectureService.startLecture(1L);

        assertEquals("Lecture is already running", result);
    }

    @Test
    void startLecture_NotFound() {
        when(lectureRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            lectureService.startLecture(1L);
        });
    }

    @Test
    void endLecture_Success() {
        lecture.setRunning(true);
        when(lectureRepository.findById(1L)).thenReturn(Optional.of(lecture));

        String result = lectureService.endLecture(1L);

        assertEquals("Lecture has ended", result);
        assertFalse(lecture.isRunning());
        verify(lectureRepository).save(lecture);
    }

    @Test
    void endLecture_AlreadyEnded() {
        lecture.setRunning(false);
        when(lectureRepository.findById(1L)).thenReturn(Optional.of(lecture));

        String result = lectureService.endLecture(1L);

        assertEquals("Lecture has already ended", result);
    }


    @Test
    void attend_InvalidOTP() {
        lecture.setRunning(true);
        lecture.setOTP("123456");
        lecture.setCourse(course);
        List<User> students = new ArrayList<>();
        students.add(user);
        course.setStudents(students);

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(lectureRepository.findById(1L)).thenReturn(Optional.of(lecture));

        String result = lectureService.attend(1L, validToken, "wrong-otp");

        assertEquals("Invalid OTP", result);
    }

    @Test
    void getLecturesByCourseId_Success() {
        List<Lecture> expectedLectures = Arrays.asList(lecture);
        when(lectureRepository.findByCourseId(1L)).thenReturn(expectedLectures);

        List<Lecture> result = lectureService.getLecturesByCourseId(1L);

        assertEquals(expectedLectures, result);
        verify(lectureRepository).findByCourseId(1L);
    }


    @Test
    void createLecture_NotInstructor() {
        Map<String, Object> request = new HashMap<>();

        assertThrows(IllegalArgumentException.class, () -> {
            lectureService.createLecture(request, invalidToken);
        });
    }
}