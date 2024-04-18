package com.example.attendance_system.util;

import com.example.attendance_system.dto.CourseDto;
import com.example.attendance_system.model.Course;

public class CourseDtoFactory {
    public static CourseDto mapToDto(Course course){
        return new CourseDto(course.getId(), course.getName(), course.getCode(), course.getTotal_hours());
    }
}
