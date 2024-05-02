package com.example.attendance_system.dto;


import java.time.LocalDate;

public record PersonDto
        (
                String name,
                String surname,
                String email,
                LocalDate birthDate,
                Integer userId) {
}
