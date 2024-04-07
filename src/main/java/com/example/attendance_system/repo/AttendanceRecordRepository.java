package com.example.attendance_system.repo;

import com.example.attendance_system.model.AttendanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Integer> {
}
