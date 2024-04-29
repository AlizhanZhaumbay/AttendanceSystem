package com.example.attendance_system.dto;

import com.example.attendance_system.model.*;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AttendanceRecordDto(
        Integer id,
        String student,

        LocalDateTime localDateTime,

        AttendanceStatus attendanceStatus,

        AttendanceType attendanceType,

        AbsenceReasonDto absenceReasonDto
) {
}
