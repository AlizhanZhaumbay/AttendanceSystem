package com.example.attendance_system.util;

import com.example.attendance_system.dto.AttendanceDto;
import com.example.attendance_system.model.Attendance;
import com.example.attendance_system.util.LessonDtoFactory;

public class AttendanceDtoFactory {
    public static AttendanceDto convertToDto(Attendance attendance) {
        return new AttendanceDto(attendance.getId(),
                LessonDtoFactory.convert(attendance.getLesson()),
                attendance.getLocalDateTime());

    }
}
