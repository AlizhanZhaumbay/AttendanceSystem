package com.example.attendance_system.controller;

import com.example.attendance_system.model.Course;
import com.example.attendance_system.model.User;
import com.example.attendance_system.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class CourseController {

    private final CourseService courseService;

    @GetMapping("/admin/courses")
    public ResponseEntity<List<Course>> getAllCoursesForAdmin() {
        return ResponseEntity.ok(courseService.getCourses());
    }

    @GetMapping("/admin/courses/{courseId}")
    public ResponseEntity<Course> getOneCourseForAdmin(
            @PathVariable("courseId") Integer courseId) {
        return ResponseEntity.ok(courseService.getCourseById(courseId));
    }

    @GetMapping("/admin/students/{studentId}/courses")
    public ResponseEntity<List<Course>> getCoursesByStudent(@PathVariable("studentId") Integer studentId) {
        return ResponseEntity.ok(courseService.getCoursesByStudent(studentId));
    }

    @GetMapping("/admin/teachers/{teacherId}/courses")
    public ResponseEntity<List<Course>> getCoursesByTeacher(@PathVariable("teacherId") Integer teacherId) {
        return ResponseEntity.ok(courseService.getCoursesByTeacher(teacherId));
    }

    @GetMapping("/student/courses")
    public ResponseEntity<List<Course>> getCoursesOfStudent() {
        return ResponseEntity.ok(courseService.getCoursesByStudent());
    }

    @GetMapping("/teacher/courses")
    public ResponseEntity<List<Course>> getCoursesOfTeacher() {
        return ResponseEntity.ok(courseService.getCoursesByTeacher());
    }
}
