package com.example.attendance_system.repo;

import com.example.attendance_system.model.AbsenceReason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AbsenceReasonRepository extends JpaRepository<AbsenceReason, Integer> {

    @Query(value = "select * from absence_reason where id in " +
            "(select absence_reason_id from attendance_record where student_id=:studentId)", nativeQuery = true)
    List<AbsenceReason> findByStudentId(Integer studentId);

    @Query(value = "select * from absence_reason where id in (select absence_reason_id from attendance_record " +
            "where attendance_id in (select id from attendance where lesson_id in (" +
            "select id from lesson where course_id=:courseId and _group=:group)))", nativeQuery = true)
    List<AbsenceReason> findByCourseAndGroup(Integer courseId, String group);

    @Query(value = "select * from absence_reason", nativeQuery = true)
    List<AbsenceReason> findAllAppeals();
}
