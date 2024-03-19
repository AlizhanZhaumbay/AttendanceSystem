package com.example.attendance_system.controller;

import com.example.attendance_system.model.Course;
import com.example.attendance_system.dto.PersonDto;
import com.example.attendance_system.service.AdminService;
import com.example.attendance_system.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
//    private final CourseService courseService;

    @GetMapping("/students")
    public ResponseEntity<List<PersonDto>> getAllStudents() {
        return ResponseEntity.ok(adminService.getAllStudents());
    }

    @GetMapping("/students/{student_id}")
    public ResponseEntity<PersonDto> getOneStudent(@PathVariable("student_id") Integer studentId) {
        Optional<PersonDto> optionalStudent = adminService.getStudentById(studentId);
        return optionalStudent
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/teachers")
    public ResponseEntity<List<PersonDto>> getAllTeachers() {
        return ResponseEntity.ok(adminService.getAllTeachers());
    }

    @GetMapping("/teachers/{teacher_id}")
    public ResponseEntity<PersonDto> getOneTeacher(@PathVariable("teacher_id")
                                                   Integer teacherId) {
        Optional<PersonDto> optionalTeacher = adminService.getTeacherById(teacherId);
        return optionalTeacher
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }






}
