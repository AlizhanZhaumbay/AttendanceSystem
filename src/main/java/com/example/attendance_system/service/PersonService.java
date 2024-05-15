package com.example.attendance_system.service;

import com.example.attendance_system.model.Course;
import com.example.attendance_system.repo.PersonRepository;
import com.example.attendance_system.util.ExceptionMessage;
import com.example.attendance_system.util.PersonDtoFactory;
import com.example.attendance_system.dto.PersonDto;
import com.example.attendance_system.exception.UserNotFoundException;
import com.example.attendance_system.model.Person;
import com.example.attendance_system.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;

//    public List<PersonDto> getAllStudents() {
//        Optional<List<Person>> optionalList = userRepository.findAllStudents();
//
//        return optionalList.map(students -> {
//            students.forEach(student -> student.setUserId(userRepository.getUserIdFromPerson(student.getId())));
//            return students.stream()
//                    .map(PersonDtoFactory::convert)
//                    .collect(Collectors.toList());
//        }).orElse(Collections.emptyList());
//    }
//
//    public List<PersonDto> getAllTeachers() {
//        Optional<List<Person>> optionalList = userRepository.findAllTeachers();
//
//        return optionalList.map(teachers -> {
//            teachers.forEach(teacher -> teacher.setUserId(userRepository.getUserIdFromPerson(teacher.getId())));
//            return teachers.stream()
//                    .map(PersonDtoFactory::convert)
//                    .collect(Collectors.toList());
//        }).orElse(Collections.emptyList());
//
//    }
//
//
//    public Optional<PersonDto> getStudentById(Integer studentId) {
//        checkStudentExists(studentId);
//
//        return userRepository.findStudentById(studentId)
//                .map(student -> {
//                    student.setUserId(studentId);
//                    return student;
//                })
//                .map(PersonDtoFactory::convert);
//
//    }
//
//    public Optional<PersonDto> getTeacherById(Integer teacherId) {
//        checkTeacherExists(teacherId);
//        return userRepository.findTeacherById(teacherId)
//                .map(teacher -> {
//                    teacher.setUserId(teacherId);
//                    return teacher;
//                })
//                .map(PersonDtoFactory::convert);
//    }

    public List<PersonDto> getAllStudents() {
        List<Person> students = personRepository.findAllStudents();

        return students
                .stream()
                .map(PersonDtoFactory::convert)
                .toList();
    }

    public List<PersonDto> getAllTeachers() {
        List<Person> teachers = personRepository.findAllTeachers();

        return teachers
                .stream()
                .map(PersonDtoFactory::convert)
                .toList();
    }

    public PersonDto getStudentById(Integer studentId) {
        checkStudentExists(studentId);

        return personRepository.findStudentById(studentId)
                .map(PersonDtoFactory::convert)
                .orElseThrow(() -> new UserNotFoundException(ExceptionMessage.studentNotFound(studentId)));

    }

    public PersonDto getTeacherById(Integer teacherId) {
        return personRepository.findTeacherById(teacherId)
                .map(PersonDtoFactory::convert)
                .orElseThrow(() -> new UserNotFoundException(ExceptionMessage.teacherNotFound(teacherId)));
    }

    public void checkStudentExists(Integer studentId) {
        if (!personRepository.existsStudentById(studentId))
            throw new UserNotFoundException(ExceptionMessage.studentNotFound(studentId));
    }

    public void checkTeacherExists(Integer teacherId) {
        if (!personRepository.existsTeacherById(teacherId))
            throw new UserNotFoundException(ExceptionMessage.teacherNotFound(teacherId));
    }

    public User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public List<PersonDto> getAllStudentsByCourseGroup(Integer courseId, String group, Integer exceptionalStudentId) {
        return personRepository.findStudentsByCourseGroup(courseId, group, exceptionalStudentId)
                .stream()
                .map(PersonDtoFactory::convert)
                .toList();
    }

    public List<PersonDto> getAllStudentsByCourseGroup(Integer courseId, String group) {
        return getAllStudentsByCourseGroup(courseId, group, 0);
    }

    public List<Person> getAllConsumersByCourse(Integer courseId, Integer id) {
        return personRepository.findAllConsumersByProducerId(courseId, id);
    }

    public List<Person> getAllProducersByCourse(Integer courseId, Integer id) {
        return personRepository.findAllProducersByConsumerId(courseId, id);
    }
}
