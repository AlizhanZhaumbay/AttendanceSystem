package com.example.attendance_system.repo;

import com.example.attendance_system.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TeacherRepository extends JpaRepository<User, Integer> {

}
