package com.example.attendance_system.repo;


import com.example.attendance_system.dto.PersonDto;
import com.example.attendance_system.model.Person;
import com.example.attendance_system.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Integer> {
    @Query("select p from Person p join User s on p.id = s.person.id " +
            "where s.role = 'STUDENT'")
    List<Person> findAllStudents();

    @Query("select p from Person p join User s on p.id = s.person.id and s.id=:studentId where s.role = 'STUDENT'")
    Optional<Person> findStudentById(@Param("studentId") Integer studentId);


    @Query("select p from Person p join User t on p.id = t.person.id " +
            "where t.role = 'TEACHER'")
    List<Person> findAllTeachers();

    @Query("select p from Person p join User s on p.id = s.person.id and s.id=:teacherId where s.role = 'TEACHER'")
    Optional<Person> findTeacherById(@Param("teacherId") Integer teacherId);

    @Query(value = "select id from _user where person_id=:personId", nativeQuery = true)
    Integer getUserIdFromPerson(Long personId);

    @Query(value = "select exists(select 1 from _user where id=:studentId and role='STUDENT')", nativeQuery = true)
    boolean existsStudentById(Integer studentId);

    @Query(value = "select exists(select 1 from _user where id=:teacherId and role='TEACHER')", nativeQuery = true)
    boolean existsTeacherById(Integer teacherId);

    @Query(value = "select * from person where id in(select person_id from _user " +
            "where id in (select student_id from enroll where lesson_id in (" +
            "select id from lesson where course_id=:courseId and _group=:group)) and id!=:exceptionalStudentId)", nativeQuery = true)
    List<Person> findStudentsByCourseGroup(Integer courseId, String group, Integer exceptionalStudentId);


    @Query(value = "select * from person where id in (select consumer_id from attendance_permission where course_id=:courseId and " +
            "producer_id=:studentId)", nativeQuery = true)
    List<Person> findAllConsumersByProducerId(Integer courseId, Integer studentId);

    @Query(value = "select * from person where id in (select producer_id from attendance_permission where course_id=:courseId and " +
            "consumer_id=:studentId)", nativeQuery = true)
    List<Person> findAllProducersByConsumerId(Integer courseId, Integer studentId);
}
