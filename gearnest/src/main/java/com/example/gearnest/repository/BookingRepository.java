package com.example.gearnest.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.gearnest.model.Booking;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByGarageId(Long garageId);

}
