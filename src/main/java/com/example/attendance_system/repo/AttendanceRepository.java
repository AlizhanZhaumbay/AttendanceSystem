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
    @Query(value = "insert into attendance_permission(producer_id, consumer_id, course_id) " +
            "VALUES (:producerId,:consumerId,:courseId)", nativeQuery = true)
    void createPermission(Integer producerId, Integer consumerId, Integer courseId);


    @Query(value = "select * from attendance where lesson_id in (" +
            "select id from lesson " +
            "where _group=:group and course_id=:courseId and teacher_id=:teacherId) order by date", nativeQuery = true)
    List<Attendance> findByCourseGroupAndTeacher(Integer courseId, String group, Integer teacherId);

    @Query(value = "SELECT CASE WHEN _limit <= 0 THEN true ELSE false END FROM attendance_permission " +
            "WHERE producer_id=:producerId AND consumer_id=:consumerId", nativeQuery = true)
    boolean checkLimitReached(Integer producerId, Integer consumerId);

    @Transactional
    @Modifying
    @Query(value = "update attendance_permission set _limit = _limit - 1 where consumer_id=:consumerId and producer_id=:producerId",
            nativeQuery = true)
    void decreaseLimit(Integer producerId, Integer consumerId);

    @Transactional
    @Modifying
    @Query(value = "update attendance_permission set _limit = 0 where consumer_id=:consumerId and producer_id=:producerId",
            nativeQuery = true)
    void resetLimit(Integer producerId, Integer consumerId);
}
