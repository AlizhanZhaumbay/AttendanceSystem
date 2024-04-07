package com.example.attendance_system.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "attendance_record")
public class AttendanceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "attendance_record_seq")
    @SequenceGenerator(name = "attendance_record_seq", allocationSize = 1)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "attendance_id")
    private Attendance attendance;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private User student;

    @Enumerated(value = EnumType.STRING)
    private AttendanceStatus attendanceStatus;

    @Enumerated(value = EnumType.STRING)
    private AttendanceType attendanceType;
}
