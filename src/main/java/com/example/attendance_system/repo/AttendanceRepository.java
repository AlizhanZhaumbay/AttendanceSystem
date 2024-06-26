package com.example.attendance_system.repo;

import com.example.attendance_system.model.Attendance;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {

    List<Attendance> findAttendancesByLessonId(Integer lessonId);

    @Query(value = "select exists(select 1 from attendance where id=:attendanceId and " +
            "lesson_id=:lessonId)", nativeQuery = true)
    boolean doesAttendanceBelongsToLesson(Integer lessonId, Integer attendanceId);

    @Modifying
    @Transactional
    @Query(value = "insert into attendance_permission(producer_id, consumer_id, lesson_id) " +
            "VALUES (:producerId,:consumerId,:lessonId)", nativeQuery = true)
    void createPermission(Integer producerId, Integer consumerId, Integer lessonId);


}
