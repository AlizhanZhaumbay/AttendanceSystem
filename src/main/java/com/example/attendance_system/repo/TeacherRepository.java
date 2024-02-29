package com.example.attendance_system.repo;

import com.example.attendance_system.model.Person;
import com.example.attendance_system.model.User;
import org.hibernate.annotations.Parameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<User, Integer> {

    @Query("select p from Person p join User t on p.id = t.person.id " +
            "where t.role = 'TEACHER'")
    Optional<List<Person>> findAllTeachers();

    @Query("select p from Person p join User s on p.id = s.person.id and s.id =:teacherId")
    Optional<Person> findTeacherById(@Param("teacherId") Integer teacherId);
}
