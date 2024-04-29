package com.example.attendance_system.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record AppealRequest(

        @NotEmpty(message = "Appeal description should not be empty or null.")
        String description,

        @NotNull(message = "Attendance Record id should not be null")
        @JsonProperty("attendance_record_id")
        Integer attendanceRecordId
) {
}
