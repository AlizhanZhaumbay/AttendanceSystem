package com.example.attendance_system.dto;

import com.example.attendance_system.model.*;

import java.time.LocalDateTime;

public record AttendanceRecordDto(

        LocalDateTime localDateTime,

        AbsenceReason absenceReason,

        AttendanceStatus attendanceStatus,

        AttendanceType attendanceType
) {
}
