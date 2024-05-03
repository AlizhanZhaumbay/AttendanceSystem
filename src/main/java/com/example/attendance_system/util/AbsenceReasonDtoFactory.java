package com.example.attendance_system.util;

import com.example.attendance_system.dto.AbsenceReasonDto;
import com.example.attendance_system.model.*;

public class AbsenceReasonDtoFactory {
    public static AbsenceReasonDto convert(AbsenceReason absenceReason){
        if(absenceReason == null){
            return AbsenceReasonDto.builder().build();
        }

        AttendanceRecord attendanceRecord = absenceReason.getAttendanceRecord();
        Person student = attendanceRecord.getStudent().getPerson();
        Lesson lesson = attendanceRecord.getAttendance().getLesson();
        Course course = lesson.getCourse();
        return AbsenceReasonDto.builder()
                .absenceReasonStatus(absenceReason.getStatus())
                .group(lesson.getGroup())
                .courseCode(course.getCode())
                .reason(absenceReason.getReason())
                .requestedDate(absenceReason.getRequestedDate())
                .student(String.format("%s %s", student.getName(), student.getSurname()))
                .build();
    }
}
