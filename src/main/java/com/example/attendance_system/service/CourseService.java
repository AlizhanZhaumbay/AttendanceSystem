package com.example.attendance_system.service;

import com.example.attendance_system.dto.CourseDto;
import com.example.attendance_system.exception.InvalidAccessException;
import com.example.attendance_system.util.CourseDtoFactory;
import com.example.attendance_system.exception.CourseNotFoundException;
import com.example.attendance_system.model.Course;
import com.example.attendance_system.model.User;
import com.example.attendance_system.repo.CourseRepository;
import com.example.attendance_system.util.ExceptionMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserService userService;

    public List<CourseDto> getCourses() {
        return courseRepository.findAll()
                .stream()
                .map(CourseDtoFactory::mapToDto)
                .toList();
    }

    public CourseDto getCourseById(Integer courseId) {
        checkCourseExists(courseId);
        Optional<Course> optionalCourse = courseRepository.findById(courseId);

        return CourseDtoFactory.mapToDto(optionalCourse.get());
    }

    public List<CourseDto> getCoursesByStudent(Integer studentId){
        userService.checkStudentExists(studentId);
        return courseRepository.findByStudentId(studentId)
                .stream()
                .map(CourseDtoFactory::mapToDto)
                .toList();
    }

    public List<CourseDto> getCoursesByCurrentStudent(){
        User student = userService.getCurrentUser();
        return courseRepository.findByStudentId(student.getId())
                .stream()
                .map(CourseDtoFactory::mapToDto)
                .toList();
    }

    public List<CourseDto> getCoursesByTeacher(Integer teacherId){
        userService.checkTeacherExists(teacherId);
        return courseRepository.findByTeacherId(teacherId)
                .stream()
                .map(CourseDtoFactory::mapToDto)
                .toList();
    }

    public List<CourseDto> getCoursesByCurrentTeacher(){
        User teacher = userService.getCurrentUser();
        return courseRepository.findByTeacherId(teacher.getId())
                .stream()
                .map(CourseDtoFactory::mapToDto)
                .collect(Collectors.toList());
    }

    public void checkCourseExists(Integer courseId){
        if(!courseRepository.existsById(courseId)){
            throw new CourseNotFoundException(String.format("No course find with id {%d}", courseId));
        }
    }

    public void isTeacherWithoutCourse(Integer courseId, Integer teacherId){
        userService.checkTeacherExists(teacherId);
        checkCourseExists(courseId);

        if(!courseRepository.hasTeacherCourse(courseId, teacherId)){
            throw new InvalidAccessException(ExceptionMessage.teacherDoesNotHaveCourse(teacherId, courseId));
        }
    }

    public void isStudentWithoutCourse(Integer courseId, Integer studentId){
        userService.checkStudentExists(studentId);
        checkCourseExists(courseId);

        if(!courseRepository.hasStudentCourse(courseId, studentId)){
            throw new InvalidAccessException(ExceptionMessage.studentDoesNotHaveCourse(studentId, courseId));
        }
    }
}
