package com.example.gearnest.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.gearnest.model.ParticularGarageService;
import com.example.gearnest.model.VehicleType;

@Repository
public interface ParticularGarageServiceRepository extends JpaRepository<ParticularGarageService, Long> {

    /**
     * Finds all services offered by a specific garage.
     */
    List<ParticularGarageService> findByGarageId(Long garageId);

    /**
     * Checks if a specific combination of garage, service, and
     * vehicle type already exists. Used for server-side validation to
     * prevent duplicates.
     */
    boolean existsByGarageIdAndServiceIdAndVehicleTypeId(Long garageId, Long serviceId, Long vehicleTypeId);

    /**
     * Retrieves all distinct vehicle types for a given garage.
     * This is used to populate the Vehicle Type dropdown on the booking page.
     */
    @Query("SELECT DISTINCT pgs.vehicleType FROM ParticularGarageService pgs WHERE pgs.garage.id = :garageId")
    List<VehicleType> findDistinctVehicleTypesByGarageId(Long garageId);

    long countByGarageId(Long garageId);
}
