package com.example.attendance_system.controller;

import com.example.attendance_system.dto.PersonDto;
import com.example.attendance_system.model.User;
import com.example.attendance_system.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;
    public static final String ADMIN_FETCH_STUDENTS = "/api/v1/admin/students";
    public static final String ADMIN_FETCH_STUDENT_BY_ID = "/api/v1/admin/students/{student_id}";
    public static final String ADMIN_FETCH_TEACHERS = "/api/v1/admin/teachers";
    public static final String ADMIN_FETCH_TEACHERS_BY_ID = "/api/v1/admin/teachers/{teacher_id}";
    public static final String SEE_TEACHER_INFO = "/api/v1/teacher";
    public static final String SEE_STUDENT_INFO = "/api/v1/student";

    @GetMapping(ADMIN_FETCH_STUDENTS)
    public ResponseEntity<List<PersonDto>> getAllStudents() {
        return ResponseEntity.ok(personService.getAllStudents());
    }

    @GetMapping(ADMIN_FETCH_STUDENT_BY_ID)
    public ResponseEntity<PersonDto> getOneStudent(@PathVariable("student_id") Integer studentId) {
        PersonDto student = personService.getStudentById(studentId);
        return ResponseEntity.ok(student);
    }

    @GetMapping(ADMIN_FETCH_TEACHERS)
    public ResponseEntity<List<PersonDto>> getAllTeachers() {
        return ResponseEntity.ok(personService.getAllTeachers());
    }

    @GetMapping(ADMIN_FETCH_TEACHERS_BY_ID)
    public ResponseEntity<PersonDto> getOneTeacher(@PathVariable("teacher_id")
                                                   Integer teacherId) {
        PersonDto teacher = personService.getTeacherById(teacherId);
        return ResponseEntity.ok(teacher);
    }

    @GetMapping(SEE_TEACHER_INFO)
    public ResponseEntity<PersonDto> getPersonalInfoForTeacher(){
        User teacher = getCurrentUser();

        return getOneTeacher(teacher.getId());
    }

    @GetMapping(SEE_STUDENT_INFO)
    public ResponseEntity<PersonDto> getPersonalInfoForStudent(){
        User student = getCurrentUser();

        return getOneStudent(student.getId());

    }

    private User getCurrentUser(){
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
