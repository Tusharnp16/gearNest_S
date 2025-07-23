package com.example.gearnest.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.gearnest.model.Admin;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Admin findByEmail(String email);
}
