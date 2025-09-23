package com.example.gearnest.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.gearnest.model.Garage;

public interface GarageRepository extends JpaRepository<Garage, Long> {

    Garage findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByPhno(String phno);
}
