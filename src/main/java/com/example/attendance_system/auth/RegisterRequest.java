package com.example.attendance_system.auth;


import com.example.attendance_system.model.Role;

public record RegisterRequest(
        String login,
        String password,
        Role role) {
}
