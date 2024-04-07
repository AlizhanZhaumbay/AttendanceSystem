package com.example.attendance_system;

import com.example.attendance_system.exception.CourseNotFoundException;
import com.example.attendance_system.model.Course;
import com.example.attendance_system.model.Person;
import com.example.attendance_system.model.Role;
import com.example.attendance_system.model.User;
import com.example.attendance_system.repo.CourseRepository;
import com.example.attendance_system.service.CourseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private CourseService courseService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void testGetCourses() {
        List<Course> courses = Collections.singletonList(new Course());
        when(courseRepository.findAll()).thenReturn(courses);

        List<Course> result = courseService.getCourses();

        verify(courseRepository, times(1)).findAll();
        assertEquals(courses, result);
    }

    @Test
    void testGetCourseByIdWhenCourseExists() {
        Course course = new Course(1L, "Math", "MATH101", 40);
        when(courseRepository.findById(1)).thenReturn(Optional.of(course));

        Course result = courseService.getCourseById(1);

        verify(courseRepository, times(1)).findById(1);
        assertEquals(course, result);
        assertEquals("MATH101", result.getCode());
        assertEquals("Math", result.getName());
        assertEquals(40, result.getTotal_hours());
    }

    @Test
    void testGetCourseByIdWhenCourseDoesNotExist() {
        when(courseRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(CourseNotFoundException.class, () -> courseService.getCourseById(1));

        verify(courseRepository, times(1)).findById(1);
    }

    @Test
    void testGetCoursesByStudent() {
        Integer studentId = 1;
        List<Course> courses = Collections.singletonList(new Course(1L, "JAVA 1", "CSS105", 40));
        when(courseRepository.findByStudentId(studentId)).thenReturn(courses);

        List<Course> result = courseService.getCoursesByStudent(studentId);

        verify(courseRepository, times(1)).findByStudentId(studentId);
        assertEquals(courses, result);
        assertEquals("JAVA 1", result.get(0).getName());
        assertEquals("CSS105", result.get(0).getCode());
    }

    @Test
    void testGetCoursesByStudentNotExist() {
        Integer studentId = 1;
        List<Course> courses = new ArrayList<>();
        when(courseRepository.findByStudentId(studentId)).thenReturn(courses);

        List<Course> result = courseService.getCoursesByStudent(studentId);

        verify(courseRepository, times(1)).findByStudentId(studentId);
        assertEquals(0, result.size());
    }



    @Test
    void testGetCoursesByTeacherWithTeacherId() {
        Integer teacherId = 1;
        List<Course> courses = Collections.singletonList(new Course());
        when(courseRepository.findByTeacherId(teacherId)).thenReturn(courses);

        List<Course> result = courseService.getCoursesByTeacher(teacherId);

        verify(courseRepository, times(1)).findByTeacherId(teacherId);
        assertEquals(courses, result);
    }

    @Test
    void testGetCoursesByTeacher() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(new User(1, "teacher", "password", Role.STUDENT, new Person()));
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        List<Course> courses = Collections.singletonList(new Course());
        when(courseRepository.findByTeacherId(1)).thenReturn(courses);

        List<Course> result = courseService.getCoursesByTeacher();

        verify(courseRepository, times(1)).findByTeacherId(1);
        assertEquals(courses, result);
    }



}

