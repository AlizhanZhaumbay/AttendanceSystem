package com.example.attendance_system.repo;

import com.example.attendance_system.model.Person;
import com.example.attendance_system.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByLogin(String login);


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

    @Query("select p from Person p where p.id =:id")
    Person getPersonById(Integer id);

    @Query(value = "select id from _user where person_id=:personId", nativeQuery = true)
    Integer getUserIdFromPerson(Long personId);

    @Query(value = "select exists (select 1 from _user where login=:login)", nativeQuery = true)
    boolean existsByLogin(String login);

    @Query(value = "select exists(select 1 from _user where id=:studentId and role='STUDENT')", nativeQuery = true)
    boolean existsStudentById(Integer studentId);

    @Query(value = "select exists(select 1 from _user where id=:teacherId and role='TEACHER')", nativeQuery = true)
    boolean existsTeacherById(Integer teacherId);
}
