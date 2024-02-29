package com.example.attendance_system.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "absence_reason")
@AllArgsConstructor
@NoArgsConstructor
public class AbsenceReason {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "attendance_id")
    private Attendance attendanceRecord;

    private String description;

    private String status;

}
