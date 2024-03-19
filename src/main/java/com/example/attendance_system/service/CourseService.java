package com.example.attendance_system.service;

import com.example.attendance_system.exception.CourseNotFoundException;
import com.example.attendance_system.model.Course;
import com.example.attendance_system.model.User;
import com.example.attendance_system.repo.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;

    public List<Course> getCourses() {
        return courseRepository.findAll();
    }

    public Course getCourseById(Integer courseId) {
        Optional<Course> optionalCourse = courseRepository.findById(courseId);

        return optionalCourse.orElseThrow(CourseNotFoundException::new);
    }

    public List<Course> getCoursesByStudent(Integer studentId){
        return courseRepository.findByStudentId(studentId);
    }

    public List<Course> getCoursesByStudent(){
        User student = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return courseRepository.findByStudentId(student.getId());
    }

    public List<Course> getCoursesByTeacher(Integer teacherId){
        return courseRepository.findByTeacherId(teacherId);
    }

    public List<Course> getCoursesByTeacher(){
        User teacher = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return courseRepository.findByTeacherId(teacher.getId());
    }
}
