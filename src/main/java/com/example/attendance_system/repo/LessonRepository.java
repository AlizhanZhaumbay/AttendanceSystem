package com.example.attendance_system.repo;

import com.example.attendance_system.model.Lesson;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

    @Query(nativeQuery = true, value = "select * from lesson where course_id=:courseId and teacher_id=:teacherId")
    List<Lesson> findByTeacher(Integer courseId, Integer teacherId);

    @Query(value = "SELECT EXISTS (SELECT 1 FROM lesson WHERE id=:lessonId AND " +
            "teacher_id=:teacherId) as attendance_exists", nativeQuery = true)
    boolean hasTeacherLesson(Integer lessonId, Integer teacherId);

    @Query(value = "SELECT EXISTS(SELECT 1 from lesson where id=:lessonId And course_id=:courseId)", nativeQuery = true)
    boolean existsByCourseAndId(Integer courseId, Integer lessonId);

    @Query(value = "SELECT EXISTS (SELECT 1 FROM lesson WHERE id in " +
            "(select lesson_id from enroll where lesson_id=:lessonId and student_id=:studentId))", nativeQuery = true)
    boolean hasStudentLesson(Integer lessonId, Integer studentId);

    @Query(value = "SELECT EXISTS(SELECT 1 from lesson where _group=:group And course_id=:courseId)", nativeQuery = true)
    boolean existsByCourseAndGroup(Integer courseId, String group);

    @Query(value = "SELECT EXISTS(SELECT 1 FROM lesson where course_id=:courseId and _group=:group " +
            "and teacher_id=:teacherId)", nativeQuery = true)
    boolean hasTeacherLesson(Integer courseId, String group, Integer teacherId);

    @Query(value = "SELECT EXISTS(SELECT 1 FROM lesson where course_id=:courseId and _group=:group " +
            "and id in(select lesson_id from enroll where student_id=:studentId))", nativeQuery = true)
    boolean hasStudentLesson(Integer courseId, String group, Integer studentId);
}
