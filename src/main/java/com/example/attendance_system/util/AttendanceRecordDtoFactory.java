package com.example.attendance_system.util;

import com.example.attendance_system.dto.AttendanceRecordDto;
import com.example.attendance_system.model.AttendanceRecord;

import java.time.LocalDateTime;

public class AttendanceRecordDtoFactory {
    public static AttendanceRecordDto convertToDto(AttendanceRecord attendanceRecord){
        LocalDateTime localDateTime = attendanceRecord.getAttendance().getLocalDateTime();
        return new AttendanceRecordDto(localDateTime, attendanceRecord.getAbsenceReason(),
                attendanceRecord.getAttendanceStatus(), attendanceRecord.getAttendanceType());
    }
}
