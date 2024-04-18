package com.example.attendance_system.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
//@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"lesson_id", "attendance_id"}))
public class QrAccessToken {

    @Id
    @Column(unique = true)
    String accessToken;

    @JoinColumn(name = "lesson_id", referencedColumnName = "id")
    @ManyToOne
    Lesson lesson;

    @ManyToOne
    @JoinColumn(name = "attendance_id", referencedColumnName = "id")
    Attendance attendance;

    LocalDateTime expiration;
}
