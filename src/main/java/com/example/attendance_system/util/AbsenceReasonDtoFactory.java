package com.example.attendance_system.util;

import com.example.attendance_system.dto.AbsenceReasonDto;
import com.example.attendance_system.model.AbsenceReason;
import com.example.attendance_system.model.AttendanceRecord;
import com.example.attendance_system.model.Person;

public class AbsenceReasonDtoFactory {
    public static AbsenceReasonDto convert(AbsenceReason absenceReason){
        if(absenceReason == null){
            return AbsenceReasonDto.builder().build();
        }

        AttendanceRecord attendanceRecord = absenceReason.getAttendanceRecord();
        Person student = attendanceRecord.getStudent().getPerson();
        return AbsenceReasonDto.builder()
                .absenceReasonStatus(absenceReason.getStatus())
                .reason(absenceReason.getReason())
                .requestedDate(absenceReason.getRequestedDate())
                .student(String.format("%s %s", student.getName(), student.getSurname()))
                .build();
    }
}
