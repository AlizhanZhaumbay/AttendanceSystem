package com.example.attendance_system.repo;

import com.example.attendance_system.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {

    @Query(value = "SELECT EXISTS (SELECT 1 FROM attendance WHERE id=:attendanceId AND " +
            "lesson_id IN (SELECT id FROM lesson WHERE teacher_id=:teacherId)) as attendance_exists", nativeQuery = true)
    boolean hasTeacherLesson(Integer attendanceId, Integer teacherId);

    @Query(value = "SELECT EXISTS " +
            "(SELECT 1 FROM attendance WHERE id=:attendanceId AND " +
            "lesson_id IN (SELECT lesson_id FROM enroll WHERE student_id=:studentId)) " +
            "AS attendance_exists", nativeQuery = true)
    boolean hasStudentLesson(Integer attendanceId, Integer studentId);

    List<Attendance> getAttendanceByLessonId(Integer lessonId);
}
