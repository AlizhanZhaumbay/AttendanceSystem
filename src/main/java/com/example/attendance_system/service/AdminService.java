package com.example.attendance_system.service;

import com.example.attendance_system.model.Person;
import com.example.attendance_system.repo.StudentRepository;
import com.example.attendance_system.repo.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;

    public List<Person> getAllStudents() {
        Optional<List<Person>> optionalList = studentRepository.findAllStudents();
        return optionalList
                .orElse(Collections.emptyList());
    }

    public List<Person> getAllTeachers() {
        Optional<List<Person>> optionalList = teacherRepository.findAllTeachers();
        return optionalList
                .orElse(Collections.emptyList());
    }


}
