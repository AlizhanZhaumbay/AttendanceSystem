package com.example.attendance_system.util;

import com.example.attendance_system.dto.AttendanceDto;
import com.example.attendance_system.dto.AttendanceRecordDto;
import com.example.attendance_system.model.AttendanceRecord;
import com.example.attendance_system.model.Person;
import com.example.attendance_system.model.User;

public class AttendanceRecordDtoFactory {
    public static AttendanceRecordDto convert(AttendanceRecord attendanceRecord) {
        AttendanceRecordDto.AttendanceRecordDtoBuilder attendanceRecordDtoBuilder =
                AttendanceRecordDto.builder()
                        .id(attendanceRecord.getId())
                        .entryTime(attendanceRecord.getEntryTime())
                        .attendanceStatus(attendanceRecord.getAttendanceStatus())
                        .attendanceType(attendanceRecord.getAttendanceType())
                        .attendanceDto(AttendanceDtoFactory.convert(attendanceRecord.getAttendance()))
                        .group(attendanceRecord.getGroup())
                        .absenceReasonDto(AbsenceReasonDtoFactory.convert(attendanceRecord.getAbsenceReason()));
        if (attendanceRecord.getStudent().getPerson() != null) {
            Person student = attendanceRecord.getStudent().getPerson();
            attendanceRecordDtoBuilder.student(String.format("%s %s", student.getName(), student.getSurname()));
        }
        User designatedUser = attendanceRecord.getDesignatedUser();
        if (designatedUser != null && designatedUser.getPerson() != null) {
            Person designatedPerson = attendanceRecord.getDesignatedUser().getPerson();
            attendanceRecordDtoBuilder.designatedPerson(
                    String.format("%s %s", designatedPerson.getName(), designatedPerson.getSurname()));
        }
        return attendanceRecordDtoBuilder.build();
    }
}
