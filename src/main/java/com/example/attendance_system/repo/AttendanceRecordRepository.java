package com.example.attendance_system.repo;

import com.example.attendance_system.model.Attendance;
import com.example.attendance_system.model.AttendanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Integer> {
    AttendanceRecord findByAttendanceIdAndStudentId(Integer attendanceId, Integer studentId);

    @Query(value = "select exists (select 1 from attendance_permission " +
            "where consumer_id=:studentId and course_id=:courseId)", nativeQuery = true)
    boolean checkStudentHaveAccessesForLesson(Integer studentId, Integer courseId);

    @Query(nativeQuery = true, value = "select exists(select 1 from attendance_permission " +
            "where producer_id=:producerId)")
    boolean existsByProducerId(Integer producerId);

    @Query(nativeQuery = true, value = "select exists(select 1 from attendance_permission " +
            "where consumer_id=:consumerId)")
    boolean existsByConsumerId(Integer consumerId);

    @Query(value = "select * from attendance_record where attendance_id in " +
            "(select id from attendance where lesson_id in (" +
            "select id from lesson where course_id=:courseId and _group=:group) order by date)", nativeQuery = true)
    List<AttendanceRecord> findByCourseIdAndGroup(Integer courseId, String group);

    @Query(value = "select * from attendance_record where student_id=:studentId and attendance_id in " +
            "(select id from attendance where lesson_id in (" +
            "select id from lesson where course_id=:courseId and _group=:group) order by date)",nativeQuery = true)
    List<AttendanceRecord> findByCourseGroupAndStudent(Integer courseId, String group, Integer studentId);

    List<AttendanceRecord> findByAttendance(Attendance attendance);
}
