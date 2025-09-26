package com.example.gearnest.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.gearnest.model.CarouselImage;

public interface CarouselImageRepository extends JpaRepository<CarouselImage, Long> {
}
