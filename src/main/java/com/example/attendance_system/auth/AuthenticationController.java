package com.example.attendance_system.auth;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@CrossOrigin
@RestController
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public static final String USER_SIGN_UP = "/api/v1/auth/register";
    public static final String USER_SIGN_IN = "/api/v1/auth/login";
    public static final String USER_REFRESH_TOKEN = "/api/v1/auth/refresh-token";

    @PostMapping(USER_SIGN_UP)
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(authenticationService.register(request));
    }
    @PostMapping(USER_SIGN_IN)
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest authenticationRequest
    ){
        return ResponseEntity.ok(authenticationService.authenticate(authenticationRequest));
    }

    @PostMapping(USER_REFRESH_TOKEN)
    public ResponseEntity<AuthenticationResponse> refreshToken(
            HttpServletRequest request
    ) {
        return ResponseEntity.ok(authenticationService.refreshToken(request));
    }
}
