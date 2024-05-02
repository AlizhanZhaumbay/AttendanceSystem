package com.example.attendance_system.util;

import com.example.attendance_system.dto.AttendanceRecordDto;
import com.example.attendance_system.model.AttendanceRecord;
import com.example.attendance_system.model.Person;

public class AttendanceRecordDtoFactory {
    public static AttendanceRecordDto convert(AttendanceRecord attendanceRecord) {
        Person student = attendanceRecord.getStudent().getPerson();
        AttendanceRecordDto.AttendanceRecordDtoBuilder attendanceRecordDtoBuilder =
                AttendanceRecordDto.builder()
                        .id(attendanceRecord.getId())
                        .student(String.format("%s %s", student.getName(), student.getSurname()))
                        .attendanceStatus(attendanceRecord.getAttendanceStatus())
                        .attendanceType(attendanceRecord.getAttendanceType())
                        .courseGroup(attendanceRecord.getGroup())
                        .time(attendanceRecord.getAttendance().getLocalDateTime());
        return attendanceRecordDtoBuilder.build();
    }
}
