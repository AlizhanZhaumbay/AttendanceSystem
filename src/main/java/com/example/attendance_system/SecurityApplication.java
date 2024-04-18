package com.example.attendance_system;

import com.example.attendance_system.model.Lesson;
import com.example.attendance_system.repo.LessonRepository;
import com.example.attendance_system.service.CourseService;
import com.example.attendance_system.service.LessonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@SpringBootApplication
@RequiredArgsConstructor
@Log4j2
public class SecurityApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecurityApplication.class, args);
    }

}
