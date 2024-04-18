package com.example.attendance_system.auth;

import jakarta.validation.constraints.NotEmpty;

public record AuthenticationRequest(
        @NotEmpty(message = "Login must not be null or empty.")
        String login,

        @NotEmpty(message = "Password must not be null or empty.")
        String password) {

}
