package com.example.attendance_system.controller;

import com.example.attendance_system.dto.LessonDto;
import com.example.attendance_system.service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;
    public static final String ADMIN_SEE_LESSONS =
            "/api/v1/admin/courses/{courseId}/lessons";
    public static final String ADMIN_SEE_LESSONS_BY_ID =
            "/api/v1/admin/courses/{courseId}/lessons/{lessonId}";
    public static final String ADMIN_SEE_LESSONS_BY_STUDENT =
            "/api/v1/admin/courses/{courseId}/students/{studentId}/lessons";
    public static final String ADMIN_SEE_LESSONS_BY_TEACHER =
            "/api/v1/admin/courses/{courseId}/teachers/{teacherId}/lessons";
    public static final String STUDENT_SEE_LESSONS =
            "/api/v1/student/courses/{courseId}/lessons";
    public static final String TEACHER_SEE_LESSONS =
            "/api/v1/teacher/courses/{courseId}/lessons";

    @GetMapping(ADMIN_SEE_LESSONS)
    public ResponseEntity<List<LessonDto>> getAllLessonsByCourseForAdmin(@PathVariable("courseId") Integer courseId) {
        return ResponseEntity.ok(lessonService.getAllLessons(courseId));
    }

    @GetMapping(ADMIN_SEE_LESSONS_BY_ID)
    public ResponseEntity<LessonDto> getOneLessonByCourseForAdmin(
            @PathVariable("courseId") Integer courseId,
            @PathVariable("lessonId") Integer lessonId) {

        return ResponseEntity.ok(lessonService.getOneLesson(courseId, lessonId));
    }

    @GetMapping(ADMIN_SEE_LESSONS_BY_STUDENT)
    public ResponseEntity<List<LessonDto>> getLessonsByStudentForAdmin(
            @PathVariable("courseId") Integer courseId,
            @PathVariable("studentId") Integer studentId) {

        return ResponseEntity.ok(lessonService.getLessonsByStudent(courseId, studentId));
    }

    @GetMapping(ADMIN_SEE_LESSONS_BY_TEACHER)
    public ResponseEntity<List<LessonDto>> getLessonsByTeacherForAdmin(
            @PathVariable("courseId") Integer courseId,
            @PathVariable("teacherId") Integer teacherId) {

        return ResponseEntity.ok(lessonService.getLessonsByTeacher(courseId, teacherId));
    }

    @GetMapping(STUDENT_SEE_LESSONS)
    public ResponseEntity<List<LessonDto>> getAllLessonsByCourseForStudent(@PathVariable("courseId") Integer courseId) {
        return ResponseEntity.ok(lessonService.getLessonsByCurrentStudent(courseId));
    }

    @GetMapping(TEACHER_SEE_LESSONS)
    public ResponseEntity<List<LessonDto>> getAllLessonsByCourseForTeacher(@PathVariable("courseId") Integer courseId) {
        return ResponseEntity.ok(lessonService.getLessonsByCurrentTeacher(courseId));
    }
}
