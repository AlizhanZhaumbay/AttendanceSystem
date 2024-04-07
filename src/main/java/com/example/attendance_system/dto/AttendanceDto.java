package com.example.attendance_system.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceDto {

    private Integer id;

    private LessonDto lessonDto;

    private LocalDateTime localDateTime;
}
