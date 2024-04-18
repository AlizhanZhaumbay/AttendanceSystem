package com.example.attendance_system.repo;

import com.example.attendance_system.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {

    @Query(nativeQuery = true, value = "select * from course where id in " +
            "(select course_id from lesson where id in " +
            "(select lesson_id from enroll where student_id=:studentId))")
    List<Course> findByStudentId(Integer studentId);

    @Query("select c from Course c where c.id in " +
            "(select l.course.id from Lesson l where l.teacher.id=:teacherId)")
    List<Course> findByTeacherId(Integer teacherId);

    @Query(value = "select exists (select 1 from course where id=:courseId and " +
            "id in (select course_id from lesson where teacher_id=:teacherId))", nativeQuery = true)
    boolean hasTeacherCourse(Integer courseId, Integer teacherId);

    @Query(value = "select exists (select 1 from course where id=:courseId and " +
            "id in (select course_id from lesson where " +
            "id in (select lesson_id from enroll where student_id=:studentId)))", nativeQuery = true)
    boolean hasStudentCourse(Integer courseId, Integer studentId);
}
