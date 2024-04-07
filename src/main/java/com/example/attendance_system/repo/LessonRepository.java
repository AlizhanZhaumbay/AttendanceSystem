package com.example.attendance_system.repo;

import com.example.attendance_system.model.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Integer> {

    @Query("select l from Lesson l where l.course.id=:courseId")
    List<Lesson> findAllByCourse(Integer courseId);

    @Query(nativeQuery = true, value = "select * from lesson where course_id=:courseId and " +
            "id in (select lesson_id from enroll where student_id=:studentId)")
    List<Lesson> findByStudent(Integer courseId, Integer studentId);

    @Query("select l from Lesson l where l.course.id=:courseId and l.teacher.id=:teacherId")
    List<Lesson> findByTeacher(Integer courseId, Integer teacherId);

    @Query("select l from Lesson l where l.course.id=:courseId and l.id=:lessonId")
    Optional<Lesson> findByCourseAndId(Integer courseId, Integer lessonId);
}
