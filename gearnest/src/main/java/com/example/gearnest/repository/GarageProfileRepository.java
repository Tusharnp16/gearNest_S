package com.example.gearnest.repository;

import com.example.gearnest.model.Garage;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GarageProfileRepository extends JpaRepository<Garage, Long> {
    List<Garage> findByIsApprovedTrueAndStatus(String status);

}

