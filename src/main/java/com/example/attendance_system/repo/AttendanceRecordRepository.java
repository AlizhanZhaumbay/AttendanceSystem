package com.example.attendance_system.repo;

import com.example.attendance_system.dto.AttendanceRecordDto;
import com.example.attendance_system.model.AttendanceRecord;
import com.example.attendance_system.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Integer> {
    AttendanceRecord findByAttendanceIdAndStudentId(Integer attendanceId, Integer studentId);

    @Query(value = "select * from attendance_record where student_id=:studentId and attendance_id in " +
            "(select id from attendance where lesson_id=:lessonId)", nativeQuery = true)
    List<AttendanceRecord> findByLessonAndStudentId(Integer lessonId, Integer studentId);

    @Query(value = "select exists (select 1 from attendance_permission " +
            "where consumer_id=:studentId and lesson_id=:lessonId and _limit > 0)", nativeQuery = true)
    boolean checkStudentHaveAccessesForLesson(Integer studentId, Integer lessonId);

    @Query(nativeQuery = true, value = "select exists(select 1 from attendance_permission " +
            "where producer_id=:producerId)")
    boolean existsByProducerId(Integer producerId);

    @Query(nativeQuery = true, value = "select exists(select 1 from attendance_permission " +
            "where consumer_id=:consumerId)")
    boolean existsByConsumerId(Integer consumerId);

    @Query(nativeQuery = true, value = "select u from _user u where id in " +
            "(select producer_id from attendance_permission " +
            "where consumer_id=:consumerId)")
    User findProducerByConsumerId(Integer consumerId);

    @Query(nativeQuery = true, value = "select * from attendance_record " +
            "where attendance_id in (select id from attendance where " +
            "lesson_id=:lessonId)")
    List<AttendanceRecord> findAllByLessonId(Integer lessonId);


}
