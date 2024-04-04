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

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    public List<PersonDto> getAllStudents() {
        Optional<List<Person>> optionalList = userRepository.findAllStudents();

        return optionalList.map(students -> students
                .stream()
                .map(PersonDtoFactory::convert)
                .collect(Collectors.toList())).orElse(Collections.emptyList());
    }

    public List<PersonDto> getAllTeachers() {
        Optional<List<Person>> optionalList = userRepository.findAllTeachers();

        return optionalList.map(teachers -> teachers
                .stream()
                .map(PersonDtoFactory::convert)
                .collect(Collectors.toList())).orElse(Collections.emptyList());

    }


    public Optional<PersonDto> getStudentById(Integer studentId) {
        return userRepository.findStudentById(studentId)
                .map(PersonDtoFactory::convert);
    }

    public Optional<PersonDto> getTeacherById(Integer teacherId) {
        return userRepository.findTeacherById(teacherId)
                .map(PersonDtoFactory::convert);
    }
}
