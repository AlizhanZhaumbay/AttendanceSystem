package com.example.attendance_system.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@RequiredArgsConstructor
@Getter
public class InvalidRequestBodyException extends RuntimeException {
    private final Set<String> errorMessages;
}
