package com.example.attendance_system.auth;


import com.example.attendance_system.model.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class AuthenticationResponse {
    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("login")
    private String login;

    @JsonProperty("role")
    private Role role;
}
