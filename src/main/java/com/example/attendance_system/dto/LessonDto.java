package com.example.attendance_system.dto;

import com.example.attendance_system.model.Course;

import java.time.LocalTime;

public record LessonDto(
        Long id,
        String teacher,
                        LocalTime startTime,
                        LocalTime endTime,
                        String dayOfWeek,
                        Course course,
                        String group) {

}
