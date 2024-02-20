package com.example.attendance_system.model;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

public enum Role {
    STUDENT,
    TEACHER,
    ADMIN;


    public SimpleGrantedAuthority getAuthority(){
        return new SimpleGrantedAuthority("ROLE_" + name());
    }
}
