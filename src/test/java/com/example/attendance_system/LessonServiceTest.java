package com.example.attendance_system;

import com.example.attendance_system.dto.LessonDto;
import com.example.attendance_system.dto.LessonDtoFactory;
import com.example.attendance_system.exception.LessonNotFoundException;
import com.example.attendance_system.model.*;
import com.example.attendance_system.repo.LessonRepository;
import com.example.attendance_system.service.LessonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class LessonServiceTest {

    @Mock
    private LessonRepository lessonRepository;

    @InjectMocks
    private LessonService lessonService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllLessons() {
        Integer courseId = 1;
        List<Lesson> lessons = Collections.singletonList(new Lesson());
        when(lessonRepository.findAllByCourse(courseId)).thenReturn(lessons);
        List<LessonDto> lessonDtos = List.of(new LessonDto());

        List<LessonDto> result = lessonService.getAllLessons(courseId);

        verify(lessonRepository, times(1)).findAllByCourse(courseId);
        assertEquals(lessonDtos, result);
    }

    @Test
    void testGetAllLessonsWhenNoLessonsFound() {
        Integer courseId = 1;
        when(lessonRepository.findAllByCourse(courseId)).thenReturn(Collections.emptyList());

        List<LessonDto> result = lessonService.getAllLessons(courseId);

        verify(lessonRepository, times(1)).findAllByCourse(courseId);
        assertEquals(Collections.emptyList(), result);
    }

    @Test
    void testGetOneLesson() {
        Integer courseId = 1;
        Integer lessonId = 1;
        Lesson lesson = new Lesson();
        when(lessonRepository.findByCourseAndId(courseId, lessonId)).thenReturn(Optional.of(lesson));

        LessonDto lessonDto = new LessonDto();
        when(LessonDtoFactory.convert(lesson)).thenReturn(lessonDto);

        LessonDto result = lessonService.getOneLesson(courseId, lessonId);

        verify(lessonRepository, times(1)).findByCourseAndId(courseId, lessonId);
        assertEquals(lessonDto, result);
    }

    @Test
    void testGetOneLessonLessonNotFound() {
        Integer courseId = 1;
        Integer lessonId = 1;
        when(lessonRepository.findByCourseAndId(courseId, lessonId)).thenReturn(Optional.empty());

        assertThrows(LessonNotFoundException.class, () -> lessonService.getOneLesson(courseId, lessonId));

        verify(lessonRepository, times(1)).findByCourseAndId(courseId, lessonId);
    }

    @Test
    void testGetLessonsByStudent() {
        Integer courseId = 1;
        Integer studentId = 1;
        List<Lesson> lessons = Collections.singletonList(new Lesson());
        when(lessonRepository.findByStudent(courseId, studentId)).thenReturn(lessons);

        List<LessonDto> lessonDtos = Collections.singletonList(new LessonDto());
        when(lessonService.mapToDto(lessons)).thenReturn(lessonDtos);

        List<LessonDto> result = lessonService.getLessonsByStudent(courseId, studentId);

        verify(lessonRepository, times(1)).findByStudent(courseId, studentId);
        assertEquals(lessonDtos, result);
    }


    @Test
    void testGetLessonsByTeacher() {
        Integer courseId = 1;
        Integer teacherId = 1;
        List<Lesson> lessons = Collections.singletonList(new Lesson());
        when(lessonRepository.findByTeacher(courseId, teacherId)).thenReturn(lessons);

        List<LessonDto> lessonDtos = Collections.singletonList(new LessonDto());
        when(lessonService.mapToDto(lessons)).thenReturn(lessonDtos);

        List<LessonDto> result = lessonService.getLessonsByTeacher(courseId, teacherId);

        verify(lessonRepository, times(1)).findByTeacher(courseId, teacherId);
        verify(lessonService, times(1)).mapToDto(lessons);
        assertEquals(lessonDtos, result);
    }

    @Test
    void testGetLessonsByTeacher_InvalidTeacherId() {
        Integer courseId = 1;
        Integer invalidTeacherId = -1;
        List<Lesson> emptyLessonList = Collections.emptyList();

        when(lessonRepository.findByTeacher(courseId, invalidTeacherId)).thenReturn(emptyLessonList);

        List<LessonDto> result = lessonService.getLessonsByTeacher(courseId, invalidTeacherId);

        verify(lessonRepository, times(1)).findByTeacher(courseId, invalidTeacherId);
        assertEquals(Collections.emptyList(), result);
    }

}
