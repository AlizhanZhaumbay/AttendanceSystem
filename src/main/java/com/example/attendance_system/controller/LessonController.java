package com.example.attendance_system.controller;

import com.example.attendance_system.dto.LessonDto;
import com.example.attendance_system.service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @GetMapping("/admin/courses/{courseId}/students/{studentId}/lessons")
    public ResponseEntity<List<LessonDto>> getLessonsByStudentForAdmin(
            @PathVariable("courseId") Integer courseId,
            @PathVariable("studentId") Integer studentId) {

        return ResponseEntity.ok(lessonService.getLessonsByStudent(courseId, studentId));
    }

    @GetMapping("/admin/courses/{courseId}/teachers/{teacherId}/lessons")
    public ResponseEntity<List<LessonDto>> getLessonsByTeacherForAdmin(
            @PathVariable("courseId") Integer courseId,
            @PathVariable("teacherId") Integer teacherId) {

        return ResponseEntity.ok(lessonService.getLessonsByTeacher(courseId, teacherId));
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
