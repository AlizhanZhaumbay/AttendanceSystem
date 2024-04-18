package com.example.attendance_system.repo;


import com.example.attendance_system.model.QrAccessToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QrAccessTokenRepository extends JpaRepository<QrAccessToken, String> {
}
