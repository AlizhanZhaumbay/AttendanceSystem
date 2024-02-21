package com.example.attendance_system.controller;

import com.example.attendance_system.model.Course;
import com.example.attendance_system.model.Person;
import com.example.attendance_system.repo.CourseRepository;
import com.example.attendance_system.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final CourseRepository courseRepository;

    @GetMapping("/students")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Person>> getAllStudents(){
        return ResponseEntity.ok(adminService.getAllStudents());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/teachers")
    public ResponseEntity<List<Person>> getAllTeachers(){
        return ResponseEntity.ok(adminService.getAllTeachers());
    }

    @GetMapping("/courses")
    public ResponseEntity<List<Course>> getAllCourses(){
        return ResponseEntity.ok(courseRepository.findAll());
    }
}
