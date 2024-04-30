package com.example.attendance_system.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LessonDto {
    Integer id;
    String teacher;
    LocalTime startTime;
    LocalTime endTime;
    String dayOfWeek;
    CourseDto courseDto;
    String group;
}
