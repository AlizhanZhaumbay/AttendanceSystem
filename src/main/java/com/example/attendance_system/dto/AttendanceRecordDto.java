package com.example.attendance_system.dto;

import com.example.attendance_system.model.AttendanceStatus;
import com.example.attendance_system.model.AttendanceType;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AttendanceRecordDto(
        Integer id,
        String courseGroup,
        LocalDateTime time,
        AttendanceStatus attendanceStatus,

        String student,

        AttendanceType attendanceType
) {
}
