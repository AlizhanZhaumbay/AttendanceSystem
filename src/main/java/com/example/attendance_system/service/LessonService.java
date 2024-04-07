package com.example.attendance_system.service;

import com.example.attendance_system.dto.LessonDto;
import com.example.attendance_system.dto.LessonDtoFactory;
import com.example.attendance_system.exception.LessonNotFoundException;
import com.example.attendance_system.model.Lesson;
import com.example.attendance_system.model.User;
import com.example.attendance_system.repo.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonService {
    private final LessonRepository lessonRepository;

    public List<LessonDto> getAllLessons(Integer courseId) {
        List<Lesson> lessons = lessonRepository.findAllByCourse(courseId);

        return mapToDto(lessons);

    }

    public LessonDto getOneLesson(Integer courseId, Integer lessonId) {
        Optional<Lesson> optionalLesson = lessonRepository.findByCourseAndId(courseId, lessonId);

        return optionalLesson
                .map(LessonDtoFactory::convert)
                .orElseThrow(LessonNotFoundException::new);
    }

    public List<LessonDto> getLessonsByStudent(Integer courseId, Integer studentId) {
        List<Lesson> lessons = lessonRepository.findByStudent(courseId, studentId);

        return mapToDto(lessons);
    }

    public List<LessonDto> getLessonsByStudent(Integer courseId) {
        User student = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Lesson> lessons = lessonRepository.findByStudent(courseId, student.getId());

        return mapToDto(lessons);
    }

    public List<LessonDto> getLessonsByTeacher(Integer courseId, Integer teacherId) {
        List<Lesson> lessons = lessonRepository.findByTeacher(courseId, teacherId);

        return mapToDto(lessons);
    }

    public List<LessonDto> getLessonsByTeacher(Integer courseId) {
        User teacher = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Lesson> lessons = lessonRepository.findByTeacher(courseId, teacher.getId());

        return mapToDto(lessons);
    }

    public List<LessonDto> mapToDto(List<Lesson> lessons){
        return lessons.stream()
                .map(LessonDtoFactory::convert)
                .collect(Collectors.toList());
    }


}
