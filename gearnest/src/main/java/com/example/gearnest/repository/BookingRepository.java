package com.example.gearnest.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.gearnest.model.Booking;

@Repository
public interface BookingRepository extends JpaRepository<Booking, String> {

    // Find all bookings for a specific garage.
    List<Booking> findByGarageId(Long garageId);

    // Find all bookings for a specific user.
    List<Booking> findByUserId(Long userId);
}
