package com.example.attendance_system.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.example.attendance_system.model.Role.*;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private static final String[] AUTH_WHITE_LIST_URL = {"/api/v1/auth/**", "/api/v1/attendance/appeal/**"};
    private static final String[] ADMIN_WHITE_LIST_URL = {"/api/v1/admin/**"};
    private static final String[] STUDENT_WHITE_LIST_URL = {"/api/v1/student/**"};
    private static final String[] TEACHER_WHITE_LIST_URL = {"/api/v1/teacher/**"};


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req ->
                        req
                                .requestMatchers(AUTH_WHITE_LIST_URL)
                                .permitAll()
                                .requestMatchers(ADMIN_WHITE_LIST_URL).hasRole(ADMIN.name())
                                .requestMatchers(STUDENT_WHITE_LIST_URL).hasRole(STUDENT.name())
                                .requestMatchers(TEACHER_WHITE_LIST_URL).hasRole(TEACHER.name())
                                .anyRequest()
                                .authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
