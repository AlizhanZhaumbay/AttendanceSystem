package com.example.attendance_system.exception;

public class AttendanceLimitHasBeenReachedException extends RuntimeException {

    public AttendanceLimitHasBeenReachedException(String message) {
        super(message);
    }
}
