package com.example.attendance_system.service;

import com.example.attendance_system.dto.PersonDtoFactory;
import com.example.attendance_system.dto.PersonDto;
import com.example.attendance_system.model.Person;
import com.example.attendance_system.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<PersonDto> getAllStudents() {
        Optional<List<Person>> optionalList = userRepository.findAllStudents();

        return optionalList.map(students -> {
            students.forEach(student -> student.setUserId(userRepository.getUserIdFromPerson(student.getId())));
            return students.stream()
                    .map(PersonDtoFactory::convert)
                    .collect(Collectors.toList());
        }).orElse(Collections.emptyList());
    }

    public List<PersonDto> getAllTeachers() {
        Optional<List<Person>> optionalList = userRepository.findAllTeachers();

        return optionalList.map(teachers -> {
            teachers.forEach(teacher -> teacher.setUserId(userRepository.getUserIdFromPerson(teacher.getId())));
            return teachers.stream()
                    .map(PersonDtoFactory::convert)
                    .collect(Collectors.toList());
        }).orElse(Collections.emptyList());

    }


    public Optional<PersonDto> getStudentById(Integer studentId) {
        return userRepository.findStudentById(studentId)
                .map(student -> {
                    student.setUserId(studentId);
                    return student;
                })
                .map(PersonDtoFactory::convert);

    }

    public Optional<PersonDto> getTeacherById(Integer teacherId) {
        return userRepository.findTeacherById(teacherId)
                .map(teacher -> {
                    teacher.setUserId(teacherId);
                    return teacher;
                })
                .map(PersonDtoFactory::convert);
    }
}
