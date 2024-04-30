package com.example.attendance_system.util;

import com.example.attendance_system.dto.AttendanceDto;
import com.example.attendance_system.model.Attendance;

public class AttendanceDtoFactory {
    public static AttendanceDto convert(Attendance attendance) {
        return new AttendanceDto(attendance.getId(),
                LessonDtoFactory.convert(attendance.getLesson()),
                attendance.getLocalDateTime());

    }
}
