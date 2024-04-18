package com.example.attendance_system.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class UserAlreadyExists extends RuntimeException{
    private final String message;
}
