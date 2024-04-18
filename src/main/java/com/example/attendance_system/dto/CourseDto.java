package com.example.attendance_system.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CourseDto(
        Long id,
        String name,
        String code,

        @JsonProperty("total_hours")
        int totalHours){
}
