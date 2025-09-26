package com.example.gearnest.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.gearnest.model.Feedback;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    @Query("SELECT f.rating FROM Feedback f WHERE f.garage.id = :garageId")
    List<Integer> findRatingsByGarageId(Long garageId);
}
