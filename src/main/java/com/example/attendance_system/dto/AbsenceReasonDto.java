package com.example.attendance_system.dto;

import com.example.attendance_system.model.AbsenceReasonStatus;
import com.example.attendance_system.model.Reason;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AbsenceReasonDto(
        Reason reason,
        @JsonProperty("status")
        AbsenceReasonStatus absenceReasonStatus,

        String student,

        LocalDateTime requestedDate,

        @JsonProperty("course_code")
        String courseCode,

        @JsonProperty("group")
        String group,

        @JsonProperty("file_path")
        String filePath) {
}
