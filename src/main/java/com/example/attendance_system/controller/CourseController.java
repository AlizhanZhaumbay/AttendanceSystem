package com.example.attendance_system.controller;

import com.example.attendance_system.dto.CourseDto;
import com.example.attendance_system.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;
    public static final String ADMIN_SEE_COURSES_BY_STUDENT = "/api/v1/admin/students/{studentId}/courses";
    public static final String ADMIN_SEE_COURSES_BY_TEACHER = "/api/v1/admin/teachers/{teacherId}/courses";
    public static final String STUDENT_SEE_COURSES = "/api/v1/student/courses";
    public static final String TEACHER_SEE_COURSES = "/api/v1/teacher/courses";
    public static final String ADMIN_SEE_COURSES = "/api/v1/admin/courses";
    public static final String ADMIN_SEE_COURSES_BY_ID = "/api/v1/admin/courses/{courseId}";

    @GetMapping(ADMIN_SEE_COURSES)
    public ResponseEntity<List<CourseDto>> getAllCoursesForAdmin() {
        return ResponseEntity.ok(courseService.getCourses());
    }

    @GetMapping(ADMIN_SEE_COURSES_BY_ID)
    public ResponseEntity<CourseDto> getOneCourseForAdmin(
            @PathVariable("courseId") Integer courseId) {
        return ResponseEntity.ok(courseService.getCourseById(courseId));
    }

    @GetMapping(ADMIN_SEE_COURSES_BY_STUDENT)
    public ResponseEntity<List<CourseDto>> getCoursesByStudent(@PathVariable("studentId") Integer studentId) {
        return ResponseEntity.ok(courseService.getCoursesByStudent(studentId));
    }

    @GetMapping(ADMIN_SEE_COURSES_BY_TEACHER)
    public ResponseEntity<List<CourseDto>> getCoursesByTeacher(@PathVariable("teacherId") Integer teacherId) {
        return ResponseEntity.ok(courseService.getCoursesByTeacher(teacherId));
    }

    @GetMapping(STUDENT_SEE_COURSES)
    public ResponseEntity<List<CourseDto>> getCoursesOfStudent() {
        return ResponseEntity.ok(courseService.getCoursesByCurrentStudent());
    }

    @GetMapping(TEACHER_SEE_COURSES)
    public ResponseEntity<List<CourseDto>> getCoursesOfTeacher() {
        return ResponseEntity.ok(courseService.getCoursesByCurrentTeacher());
    }
}
