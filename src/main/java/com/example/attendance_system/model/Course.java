package com.example.attendance_system.model;


import jakarta.persistence.*;
import lombok.*;

import java.util.List;


@Data
@Entity
@Table(name = "course")
@AllArgsConstructor
@NoArgsConstructor
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    @Column(unique = true)
    private String code;

    private int total_hours;

    @OneToMany(mappedBy = "course")
    List<Lesson> lessons;
}
