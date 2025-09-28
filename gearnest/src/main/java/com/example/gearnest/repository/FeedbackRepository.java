package com.example.gearnest.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.gearnest.model.Feedback;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    // Method required by the GarageFeedbackController to fetch all reviews for a
    // specific garage.
    List<Feedback> findByGarageId(Long garageId);

    // Existing method to fetch ratings
    @Query("SELECT f.rating FROM Feedback f WHERE f.garage.id = :garageId")
    List<Integer> findRatingsByGarageId(Long garageId);
}
