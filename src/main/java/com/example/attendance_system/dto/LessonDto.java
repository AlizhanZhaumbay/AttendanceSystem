package com.example.attendance_system.dto;

import com.example.attendance_system.model.Course;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LessonDto {
    Long id;
    String teacher;
    LocalTime startTime;
    LocalTime endTime;
    String dayOfWeek;
    Course course;
    String group;

}
