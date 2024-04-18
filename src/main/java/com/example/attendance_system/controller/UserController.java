package com.example.attendance_system.controller;

import com.example.attendance_system.dto.PersonDto;
import com.example.attendance_system.model.User;
import com.example.attendance_system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("admin/students")
    public ResponseEntity<List<PersonDto>> getAllStudents() {
        return ResponseEntity.ok(userService.getAllStudents());
    }

    @GetMapping("admin/students/{student_id}")
    public ResponseEntity<PersonDto> getOneStudent(@PathVariable("student_id") Integer studentId) {
        PersonDto student = userService.getStudentById(studentId);
        return ResponseEntity.ok(student);
    }

    @GetMapping("admin/teachers")
    public ResponseEntity<List<PersonDto>> getAllTeachers() {
        return ResponseEntity.ok(userService.getAllTeachers());
    }

    @GetMapping("admin/teachers/{teacher_id}")
    public ResponseEntity<PersonDto> getOneTeacher(@PathVariable("teacher_id")
                                                   Integer teacherId) {
        PersonDto teacher = userService.getTeacherById(teacherId);
        return ResponseEntity.ok(teacher);
    }

    @GetMapping("teacher")
    public ResponseEntity<PersonDto> getPersonalInfoForTeacher(){
        User teacher = getCurrentUser();

        return getOneTeacher(teacher.getId());
    }

    @GetMapping("student")
    public ResponseEntity<PersonDto> getPersonalInfoForStudent(){
        User student = getCurrentUser();

        return getOneStudent(student.getId());

    }

    private User getCurrentUser(){
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
