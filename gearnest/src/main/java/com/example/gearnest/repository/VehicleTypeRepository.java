package com.example.gearnest.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.gearnest.model.VehicleType;

public interface VehicleTypeRepository extends JpaRepository<VehicleType, Long> {

    /**
     * This new method will check if a vehicle type with the given name
     * already exists in the database, ignoring upper/lower case.
     * It's used for validation to prevent duplicate names.
     */
    boolean existsByNameIgnoreCase(String name);

}