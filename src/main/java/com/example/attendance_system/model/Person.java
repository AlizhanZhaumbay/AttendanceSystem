package com.example.attendance_system.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;


@Data
@Entity
@Table(name = "person")
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Transient
    private Integer userId;

    private String name;

    private String surname;

    private LocalDate birthDate;

    private String email;
}
