package com.example.attendance_system.dto;


import java.time.LocalDate;

public record PersonDto
        (
                Integer userId,
                String name,
                String surname,
                String email,
                LocalDate birthDate) {
}
