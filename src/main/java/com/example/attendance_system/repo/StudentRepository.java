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
public interface StudentRepository extends JpaRepository<User, Integer> {
    @Query("select p from Person p join User s on p.id = s.person.id " +
            "where s.role = 'STUDENT'")
    Optional<List<Person>> findAllStudents();

    @Query("select p from Person p join User s on p.id = s.person.id and s.id =: studentId")
    Optional<Person> findStudentById(@Param("studentId") Integer studentId);
}
