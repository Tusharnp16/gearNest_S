package com.example.gearnest.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.gearnest.model.Cities;

@Repository
public interface CityRepository extends JpaRepository<Cities, Long> {
    List<Cities> findByStateId(Long stateId);
}
