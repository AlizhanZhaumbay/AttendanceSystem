package com.example.attendance_system.util;

import com.example.attendance_system.dto.AttendanceRecordDto;
import com.example.attendance_system.model.AttendanceRecord;
import com.example.attendance_system.model.Person;

import java.time.LocalDateTime;

public class AttendanceRecordDtoFactory {
    public static AttendanceRecordDto convertToDto(AttendanceRecord attendanceRecord){
        LocalDateTime localDateTime = attendanceRecord.getAttendance().getLocalDateTime();
        Person student = attendanceRecord.getStudent().getPerson();
        AttendanceRecordDto.AttendanceRecordDtoBuilder attendanceRecordDtoBuilder =
                AttendanceRecordDto.builder()
                .localDateTime(localDateTime)
                .attendanceStatus(attendanceRecord.getAttendanceStatus())
                .attendanceType(attendanceRecord.getAttendanceType())
                .absenceReason(attendanceRecord.getAbsenceReason());
        if(student != null){
              return attendanceRecordDtoBuilder.student(student.getName() + " " + student.getSurname()).build();
        }
        return attendanceRecordDtoBuilder.build();
    }
}
