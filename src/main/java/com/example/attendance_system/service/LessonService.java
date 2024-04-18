package com.example.attendance_system.service;

import com.example.attendance_system.dto.LessonDto;
import com.example.attendance_system.util.ExceptionMessage;
import com.example.attendance_system.util.LessonDtoFactory;
import com.example.attendance_system.exception.InvalidAccessException;
import com.example.attendance_system.exception.LessonNotFoundException;
import com.example.attendance_system.model.Lesson;
import com.example.attendance_system.model.User;
import com.example.attendance_system.repo.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonService {
    private final LessonRepository lessonRepository;
    private final CourseService courseService;
    private final UserService userService;

    public List<LessonDto> getAllLessons(Integer courseId) {
        courseService.checkCourseExists(courseId);
        List<Lesson> lessons = lessonRepository.findAllByCourse(courseId);

        return mapToDto(lessons);

    }

    public LessonDto getOneLesson(Integer courseId, Integer lessonId) {
        courseService.checkCourseExists(courseId);
        checkLessonExistsWithCourse(courseId, lessonId);
        return LessonDtoFactory.convert(getLessonById(lessonId));
    }

    public Lesson getLessonById(Integer lessonId){
        return lessonRepository.findById(lessonId)
                .orElseThrow(() -> new LessonNotFoundException(ExceptionMessage.lessonNotFound(lessonId)));
    }

    public List<LessonDto> getLessonsByStudent(Integer courseId, Integer studentId) {
        courseService.isStudentWithoutCourse(courseId, studentId);
        List<Lesson> lessons = lessonRepository.findByStudent(courseId, studentId);

        return mapToDto(lessons);
    }

    public List<LessonDto> getLessonsByCurrentStudent(Integer courseId) {
        User student = userService.getCurrentUser();
        courseService.isStudentWithoutCourse(courseId, student.getId());
        List<Lesson> lessons = lessonRepository.findByStudent(courseId, student.getId());

        return mapToDto(lessons);
    }

    public List<LessonDto> getLessonsByTeacher(Integer courseId, Integer teacherId) {
        courseService.isTeacherWithoutCourse(courseId, teacherId);
        List<Lesson> lessons = lessonRepository.findByTeacher(courseId, teacherId);
        return mapToDto(lessons);
    }

    public List<LessonDto> getLessonsByCurrentTeacher(Integer courseId) {
        User teacher = userService.getCurrentUser();
        courseService.isTeacherWithoutCourse(courseId, teacher.getId());
        List<Lesson> lessons = lessonRepository.findByTeacher(courseId, teacher.getId());

        return mapToDto(lessons);
    }

    private List<LessonDto> mapToDto(List<Lesson> lessons){
        return lessons.stream()
                .map(LessonDtoFactory::convert)
                .collect(Collectors.toList());
    }


    public void checkLessonExistsWithCourse(Integer courseId, Integer lessonId){
        checkLessonExists(lessonId);
        if(!lessonRepository.existsByCourseAndId(courseId, lessonId))
            throw new LessonNotFoundException(ExceptionMessage.lessonNotFoundWithCourse(lessonId, courseId));
    }

    public void checkLessonExists(Integer lessonId){
        if(!lessonRepository.existsById(lessonId)){
            throw new LessonNotFoundException(ExceptionMessage.lessonNotFound(lessonId));
        }
    }

    public void isTeacherWithoutLesson(Integer lessonId, Integer teacherId){
        if(!lessonRepository.hasTeacherLesson(lessonId, teacherId)){
            throw new InvalidAccessException(ExceptionMessage.teacherDoesNotHaveLesson(teacherId, lessonId));
        }
    }

    public void isStudentWithoutLesson(Integer lessonId, Integer studentId){
        if(!lessonRepository.hasStudentLesson(lessonId, studentId)){
            throw new InvalidAccessException(ExceptionMessage.studentDoesNotHaveLesson(studentId, lessonId));
        }
    }
}
