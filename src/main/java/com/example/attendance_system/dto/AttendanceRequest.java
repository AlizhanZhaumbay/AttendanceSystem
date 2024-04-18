package com.example.attendance_system.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AttendanceRequest {

        @NotNull(message = "Attendance Id must be greater than or equal to 0")
        @Min(value = 0, message = "Attendance Id must be greater than or equal to 0")
        @JsonProperty("attendance_id")
        Integer attendanceId;

        @NotNull(message = "Lesson Id must be greater than or equal to 0")
        @Min(value = 0, message = "Lesson Id must be greater than or equal to 0")
        @JsonProperty("lesson_id")
        Integer lessonId;
}
