package com.example.attendance_system.dto;

import lombok.Data;

import java.time.LocalDateTime;

public record PersonDto(String name, String surname, String email, LocalDateTime birthDate) {
}
