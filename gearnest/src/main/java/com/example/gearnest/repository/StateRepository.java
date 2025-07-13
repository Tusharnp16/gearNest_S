package com.example.gearnest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.gearnest.model.States;

@Repository
public interface StateRepository extends JpaRepository<States, Long> {
    
}
