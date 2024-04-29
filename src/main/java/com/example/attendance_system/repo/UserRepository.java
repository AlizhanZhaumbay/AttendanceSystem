package com.example.attendance_system.repo;

import com.example.attendance_system.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByLogin(String login);

    @Query(value = "select exists (select 1 from _user where login=:login)", nativeQuery = true)
    boolean existsByLogin(String login);

    @Query(nativeQuery = true, value = "select * from _user where id in " +
            "(select producer_id from attendance_permission where consumer_id=:consumerId)")
    User findProducerByConsumerId(Integer consumerId);
}
