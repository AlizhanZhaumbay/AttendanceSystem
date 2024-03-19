package com.example.attendance_system.auth;


import com.example.attendance_system.model.Role;
import com.fasterxml.jackson.annotation.JsonProperty;

public record RegisterRequest(
        String login,
        String password,
        Role role,

        @JsonProperty("person_id")
        Integer personId) {
}
