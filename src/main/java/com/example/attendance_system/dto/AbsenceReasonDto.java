package com.example.attendance_system.dto;

import com.example.attendance_system.model.AbsenceReasonStatus;
import com.example.attendance_system.model.Reason;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record AbsenceReasonDto(
        Reason reason,
        @JsonProperty("status")
        AbsenceReasonStatus absenceReasonStatus,

        @JsonProperty("file_path")
        String filePath) {
}
