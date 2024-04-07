package com.example.attendance_system.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
@Entity
@Table(name = "person")
@AllArgsConstructor
@NoArgsConstructor
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "person")
    private User user;

    private String name;

    private String surname;

    private LocalDate birthDate;

    private String email;


    public Integer getUserId(){
        return user.getId();
    }
}
