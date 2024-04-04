package com.example.attendance_system.controller;

import com.example.attendance_system.dto.LessonDto;
import com.example.attendance_system.model.Course;
import com.example.attendance_system.model.Lesson;
import com.example.attendance_system.model.User;
import com.example.attendance_system.service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000/")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;

    @GetMapping("/admin/courses/{courseId}/lessons")
    public ResponseEntity<List<LessonDto>> getAllLessonsByCourseForAdmin(@PathVariable("courseId") Integer courseId) {
        return ResponseEntity.ok(lessonService.getAllLessons(courseId));
    }

    @GetMapping("/admin/courses/{courseId}/lessons/{lessonId}")
    public ResponseEntity<LessonDto> getOneLessonByCourseForAdmin(
            @PathVariable("courseId") Integer courseId,
            @PathVariable("lessonId") Integer lessonId) {

        return ResponseEntity.ok(lessonService.getOneLesson(courseId, lessonId));
    }

    @GetMapping("/student/courses/{courseId}/lessons")
    public ResponseEntity<List<LessonDto>> getAllLessonsByCourseForStudent(@PathVariable("courseId") Integer courseId) {
        return ResponseEntity.ok(lessonService.getLessonsByStudent(courseId));
    }

    @GetMapping("/teacher/courses/{courseId}/lessons")
    public ResponseEntity<List<LessonDto>> getAllLessonsByCourseForTeacher(@PathVariable("courseId") Integer courseId) {
        return ResponseEntity.ok(lessonService.getLessonsByTeacher(courseId));
    }
}
