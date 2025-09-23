package com.example.gearnest.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.gearnest.model.ParticularGarageService;

public interface ParticularGarageServiceRepository extends JpaRepository<ParticularGarageService, Long> {

    /**
     * Finds all services offered by a specific garage.
     * Used to display the list on the garage's "Manage Services" page.
     */
    List<ParticularGarageService> findByGarageId(Long garageId);

    /**
     * NEW METHOD: Checks if a specific combination of garage, service, and
     * vehicle type already exists. Used for server-side validation to
     * prevent duplicates.
     */
    boolean existsByGarageIdAndServiceIdAndVehicleTypeId(Long garageId, Long serviceId, Long vehicleTypeId);
}