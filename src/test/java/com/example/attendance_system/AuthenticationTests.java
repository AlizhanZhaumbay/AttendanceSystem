package com.example.attendance_system;

import com.example.attendance_system.auth.AuthenticationRequest;
import com.example.attendance_system.auth.RegisterRequest;
import com.example.attendance_system.model.Role;
import com.example.attendance_system.repo.TokenRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
public class AuthenticationTests {

    @Autowired
    MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String BASE_URL = "http://localhost:8080/api/v1/auth";

    @Test
    @Transactional
    @SneakyThrows
    @DisplayName("Post request should send accessToken with status 200")
    void handleAuthentication_ReturnsValidResponseAccessToken() {
        var login = "/login";
        var register = "/register";

        RegisterRequest registerRequest =
                new RegisterRequest("login", "password", Role.STUDENT, null);
        AuthenticationRequest authenticationRequest = new AuthenticationRequest(
                "login", "password"
        );


        var registerRequestBuilder = post(BASE_URL + register)
                .content(objectMapper.writeValueAsString(registerRequest))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(registerRequestBuilder).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        var loginRequestBuilder = post(BASE_URL + login)
                .content(objectMapper.writeValueAsString(authenticationRequest))
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(loginRequestBuilder)
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON)
                );

    }
}
