package com.example.attendance_system.service;

import com.example.attendance_system.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Set;

@RestControllerAdvice
public class ExceptionHandlerService {

    @ExceptionHandler({
            LessonNotFoundException.class,
            CourseNotFoundException.class,
            UserNotFoundException.class,
            TokenExpiredException.class,
            AttendanceNotFoundException.class})
    public ResponseEntity<String> notFoundExceptions(RuntimeException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({UserAlreadyExists.class})
    public ResponseEntity<String> conflictExceptions(RuntimeException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler({InvalidAccessException.class})
    public ResponseEntity<String> invalidAccessExceptions(RuntimeException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({InvalidRequestBodyException.class})
    public ResponseEntity<Set<String>> invalidRequestBodyException(InvalidRequestBodyException exception){
        return ResponseEntity
                .badRequest()
                .body(exception.getErrorMessages());
    }

    @ExceptionHandler({AttendanceLimitHasBeenReachedException.class})
    public ResponseEntity<String> attendanceLimitHasBeenReachedException(AttendanceLimitHasBeenReachedException exception){
        return ResponseEntity.ok(exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String message = "Invalid parameter value: " + ex.getName() + ".";
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<String> authenticationFailedException(AuthenticationException exception){
        return new ResponseEntity<>("Invalid login or password.", HttpStatus.FORBIDDEN);
    }
}
