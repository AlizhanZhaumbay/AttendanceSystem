package com.example.attendance_system.dto;

import com.example.attendance_system.model.*;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AttendanceRecordDto(
        String student,

        LocalDateTime localDateTime,

        AbsenceReason absenceReason,

        AttendanceStatus attendanceStatus,

        AttendanceType attendanceType
) {
}
