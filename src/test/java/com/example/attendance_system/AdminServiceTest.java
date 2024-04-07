package com.example.attendance_system;

import com.example.attendance_system.dto.PersonDto;
import com.example.attendance_system.model.Person;
import com.example.attendance_system.repo.UserRepository;
import com.example.attendance_system.service.AdminService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AdminService adminService;


    @Test
    void testGetAllStudents() {
        List<Person> students = Arrays.asList(
                new Person(1L, "Dana", "Amangeldi", LocalDate.of(2004, 9, 26), "gmail"),
                new Person(2L, "Dauren", "Yesmukhanov", LocalDate.of(2004, 6, 3), "gmail")
        );
        when(userRepository.findAllStudents()).thenReturn(Optional.of(students));

        List<PersonDto> studentDtos = adminService.getAllStudents();

        assertEquals(2, studentDtos.size());
        assertEquals("Dana", studentDtos.get(0).getName());
        assertEquals("Amangeldi", studentDtos.get(0).getSurname());
        assertEquals("Dauren", studentDtos.get(1).getName());
        assertEquals("Yesmukhanov", studentDtos.get(1).getSurname());
        assertEquals(LocalDate.of(2004, 9, 26), studentDtos.get(0).getBirthDate());
        assertEquals(LocalDate.of(2004, 6, 3), studentDtos.get(1).getBirthDate());

        verify(userRepository, times(1)).findAllStudents();
    }

    @Test
    public void testGetAllTeachersWhenTeachersExist() {

        List<Person> teachers = Arrays.asList(
                new Person(1L, "Dana", "Amangeldi", LocalDate.of(2004, 9, 26), "gmail"),
                new Person(2L, "Dauren", "Yesmukhanov", LocalDate.of(2004, 6, 3), "gmail")
        );

        when(userRepository.findAllTeachers()).thenReturn(Optional.of(teachers));

        List<PersonDto> teacherDtos = adminService.getAllTeachers();

        verify(userRepository, times(1)).findAllTeachers();

        assertEquals(2, teacherDtos.size());
        assertEquals("Dana", teacherDtos.get(0).getName());
        assertEquals("Amangeldi", teacherDtos.get(0).getSurname());
        assertEquals("Dauren", teacherDtos.get(1).getName());
        assertEquals("Yesmukhanov", teacherDtos.get(1).getSurname());
    }

    @Test
    public void testGetAllTeachersWhenNoTeachersExist() {
        when(userRepository.findAllTeachers()).thenReturn(Optional.empty());

        List<PersonDto> teacherDtos = adminService.getAllTeachers();

        verify(userRepository, times(1)).findAllTeachers();

        assertEquals(Collections.emptyList(), teacherDtos);
    }

    @Test
    public void testGetStudentByIdWhenStudentExists() {
        Person student = new Person(1L, "Dana", "Amangeldi", LocalDate.of(2004, 9, 26), "gmail");

        when(userRepository.findStudentById(1)).thenReturn(Optional.of(student));

        Optional<PersonDto> studentDtoOptional = adminService.getStudentById(1);

        verify(userRepository, times(1)).findStudentById(1);

        assertTrue(studentDtoOptional.isPresent());
        assertEquals("Dana", studentDtoOptional.get().getName());
        assertEquals("Amangeldi", studentDtoOptional.get().getSurname());
        assertEquals("gmail", studentDtoOptional.get().getEmail());
    }

    @Test
    public void testGetStudentByIdWhenStudentDoesNotExist() {
        when(userRepository.findStudentById(1)).thenReturn(Optional.empty());

        Optional<PersonDto> studentDtoOptional = adminService.getStudentById(1);

        verify(userRepository, times(1)).findStudentById(1);

        assertFalse(studentDtoOptional.isPresent());
    }

    @Test
    public void testGetTeacherByIdWhenTeacherExists() {
        Person teacher = new Person(1L, "Dana", "Amangeldi", LocalDate.of(2004, 9, 26), "dana@gmail.com");

        when(userRepository.findTeacherById(1)).thenReturn(Optional.of(teacher));

        Optional<PersonDto> teacherDtoOptional = adminService.getTeacherById(1);

        verify(userRepository, times(1)).findTeacherById(1);

        assertTrue(teacherDtoOptional.isPresent());
        assertEquals("Dana", teacherDtoOptional.get().getName());
        assertEquals("Amangeldi", teacherDtoOptional.get().getSurname());
    }

    @Test
    public void testGetTeacherByIdWhenTeacherDoesNotExist() {
        when(userRepository.findTeacherById(1)).thenReturn(Optional.empty());

        Optional<PersonDto> teacherDtoOptional = adminService.getTeacherById(1);

        verify(userRepository, times(1)).findTeacherById(1);

        assertFalse(teacherDtoOptional.isPresent());
    }





}
