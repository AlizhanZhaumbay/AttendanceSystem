package com.example.attendance_system.util;

import com.example.attendance_system.dto.AttendanceRecordDto;
import com.example.attendance_system.model.AttendanceRecord;

public class AttendanceRecordDtoFactory {
    public static AttendanceRecordDto convert(AttendanceRecord attendanceRecord) {
        AttendanceRecordDto.AttendanceRecordDtoBuilder attendanceRecordDtoBuilder =
                AttendanceRecordDto.builder()
                        .id(attendanceRecord.getId())
                        .attendanceStatus(attendanceRecord.getAttendanceStatus())
                        .attendanceType(attendanceRecord.getAttendanceType())
                        .courseGroup(attendanceRecord.getGroup())
                        .time(attendanceRecord.getAttendance().getLocalDateTime());
        return attendanceRecordDtoBuilder.build();
    }
}
