package com.example.attendance_system.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "person")
@AllArgsConstructor
@NoArgsConstructor
public class Person {
    @Id
    @GeneratedValue
    private Long id;

    @OneToOne(mappedBy = "person")
    private User user;

    private String name;

    private String surname;

    private LocalDateTime birthDate;

    private String email;
}
