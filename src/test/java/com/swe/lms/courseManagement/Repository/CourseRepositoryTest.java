package com.swe.lms.courseManagement.Repository;

import com.swe.lms.courseManagement.entity.Course;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class CourseRepositoryTest {
    //given - when - then

    @Autowired
    private CourseRepository courseRepository;
    Course course;

    @BeforeEach
    void setUp() {
        course = new Course();
        course.setName("Maths");
        course.setCode("M111");
        courseRepository.save(course);
    }

    @AfterEach
    void tearDown() {
        course = null;
        courseRepository.deleteAll();
    }

    @Test
    void testFindByNameOrCode_found(){
    Course test  = courseRepository.findByNameOrCode("Maths","M111");
    assertEquals(test.getCode(),course.getCode());
    assertEquals(test.getName(),course.getName());
    }


    @Test
    void testFindByNameOrCode_notFound(){
        Course test = courseRepository.findByNameOrCode("Algorithms","A111");
        assertThat(test).isNull();

    }
}
