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
@RequestMapping("/api/v1")
public class CourseController {

    private final CourseService courseService;

    @GetMapping("/admin/courses")
    public ResponseEntity<List<CourseDto>> getAllCoursesForAdmin() {
        return ResponseEntity.ok(courseService.getCourses());
    }

    @GetMapping("/admin/courses/{courseId}")
    public ResponseEntity<CourseDto> getOneCourseForAdmin(
            @PathVariable("courseId") Integer courseId) {
        return ResponseEntity.ok(courseService.getCourseById(courseId));
    }

    @GetMapping("/admin/students/{studentId}/courses")
    public ResponseEntity<List<CourseDto>> getCoursesByStudent(@PathVariable("studentId") Integer studentId) {
        return ResponseEntity.ok(courseService.getCoursesByStudent(studentId));
    }

    @GetMapping("/admin/teachers/{teacherId}/courses")
    public ResponseEntity<List<CourseDto>> getCoursesByTeacher(@PathVariable("teacherId") Integer teacherId) {
        return ResponseEntity.ok(courseService.getCoursesByTeacher(teacherId));
    }

    @GetMapping("/student/courses")
    public ResponseEntity<List<CourseDto>> getCoursesOfStudent() {
        return ResponseEntity.ok(courseService.getCoursesByCurrentStudent());
    }

    @GetMapping("/teacher/courses")
    public ResponseEntity<List<CourseDto>> getCoursesOfTeacher() {
        return ResponseEntity.ok(courseService.getCoursesByCurrentTeacher());
    }
}
