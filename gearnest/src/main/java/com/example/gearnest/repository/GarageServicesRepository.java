package com.example.gearnest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.gearnest.model.GarageServices;

@Repository
public interface GarageServicesRepository extends JpaRepository<GarageServices, Long> {
    boolean existsByNameIgnoreCase(String name);
}
