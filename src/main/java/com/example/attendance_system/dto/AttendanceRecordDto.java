package com.example.attendance_system.dto;

import com.example.attendance_system.model.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.LocalTime;

@Builder
public record AttendanceRecordDto(
        Integer id,

        Attendance attendance,

        String student,

        AbsenceReasonDto absenceReasonDto,

        AttendanceStatus attendanceStatus,

        AttendanceType attendanceType,

        String designatedPerson,

        LocalTime entryTime
) {
}
