package com.example.attendance_system.controller;

import com.example.attendance_system.model.Course;
import com.example.attendance_system.model.Person;
import com.example.attendance_system.model.User;
import com.example.attendance_system.repo.CourseRepository;
import com.example.attendance_system.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    // TODO instead of using repo   private final AdminService adminService;
    private final UserRepository userRepository;

    // TODO instead of using repo   private final CourseService courseService;
    private final CourseRepository courseRepository;

    //TODO use student service layer
    @GetMapping("/students")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Person> getAllStudents(){
        List<Person> allStudents = userRepository.findAllStudents();
        return allStudents;
    }

    //TODO use teacher service layer
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/teachers")
    public List<Person> getAllTeachers(){
        return userRepository.findAllTeachers();
    }

    //TODO use course service layer
    @GetMapping("/courses")
    public List<Course> getAllCourses(){
        return courseRepository.findAll();
    }
}
