package com.example.attendance_system.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@Entity
@Table(name = "absence_reason")
@AllArgsConstructor
@NoArgsConstructor
public class AbsenceReason {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "absenceReason")
    private AttendanceRecord attendanceRecord;

    private String reason;

    @Enumerated(EnumType.STRING)
    private AbsenceReasonStatus status;

    private String filePath;

    private LocalDateTime requestedDate;
}
