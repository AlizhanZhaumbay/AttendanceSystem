package com.example.attendance_system.util;

import com.example.attendance_system.dto.AbsenceReasonDto;
import com.example.attendance_system.model.AbsenceReason;

public class AbsenceReasonDtoFactory {
    public static AbsenceReasonDto convert(AbsenceReason absenceReason){
        if(absenceReason == null){
            return AbsenceReasonDto.builder().build();
        }
        return new AbsenceReasonDto(
                absenceReason.getReason(),
                absenceReason.getStatus(),
                absenceReason.getFilePath());
    }
}
