package com.example.attendance_system.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "attendance_record")
@ToString
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

    @OneToOne
    @JoinColumn(name = "absence_reason_id", referencedColumnName = "id")
    private AbsenceReason absenceReason;

    @Enumerated(value = EnumType.STRING)
    private AttendanceStatus attendanceStatus;

    @Enumerated(value = EnumType.STRING)
    private AttendanceType attendanceType;

    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "designated_person")
    private User designatedUser;

    private LocalTime entryTime;

    @Column(name = "_group")
    private String group;
}
