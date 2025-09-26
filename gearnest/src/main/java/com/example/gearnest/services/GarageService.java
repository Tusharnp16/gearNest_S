package com.example.gearnest.services;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.gearnest.dto.GarageCardDTO;
import com.example.gearnest.model.Garage;
import com.example.gearnest.model.ParticularGarageService;
import com.example.gearnest.repository.FeedbackRepository;
import com.example.gearnest.repository.GarageRepository;
import com.example.gearnest.repository.ParticularGarageServiceRepository;

/**
 * Service layer for handling business logic related to Garages.
 * This class is responsible for fetching data from repositories, processing it,
 * and preparing it for the controller.
 * As of Tuesday, September 23, 2025, this service powers the main dashboard of
 * GearNest Surat.
 */
@Service
public class GarageService {

    @Autowired
    private GarageRepository garageRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private ParticularGarageServiceRepository particularGarageServiceRepository;

    /**
     * Prepares the complete list of GarageCardDTOs needed for the dashboard view.
     * It fetches only approved garages and enriches them with calculated data
     * like average ratings and a formatted list of services.
     *
     * @return A list of DTOs, ready for rendering in the view.
     */
    public List<GarageCardDTO> getGarageCardsForDashboard() {
        // Step 1: Fetch only approved garages from the database.
        List<Garage> approvedGarages = garageRepository.findByIsApprovedTrue();

        // Step 2: Convert each Garage entity to a view-friendly GarageCardDTO.
        return approvedGarages.stream()
                .map(this::mapEntityToDto)
                .collect(Collectors.toList());
    }

    /**
     * Converts a single Garage database entity into a view-ready GarageCardDTO.
     * This method performs the core transformation logic, including rating
     * calculation.
     *
     * @param garage The raw entity from the database.
     * @return The formatted DTO containing only the necessary data for the view.
     */
    private GarageCardDTO mapEntityToDto(Garage garage) {
        GarageCardDTO dto = new GarageCardDTO();

        // --- Basic Info Mapping ---
        dto.setId(garage.getId());
        dto.setGarageName(garage.getGarageName());
        dto.setAddress(garage.getAddress());
        dto.setCardImage(garage.getCardImage());
        dto.setDescription(garage.getDescription());
        dto.setProfileImage(garage.getProfileImage());

        // --- Nested City DTO Mapping ---
        GarageCardDTO.CityDTO cityDTO = new GarageCardDTO.CityDTO();
        cityDTO.setName(garage.getCity().getName());
        dto.setCity(cityDTO);

        // --- Average Rating Calculation ---
        List<Integer> ratings = feedbackRepository.findRatingsByGarageId(garage.getId());
        double averageRating = ratings.stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0); // Default to 0.0 if there are no ratings
        dto.setAverageRating(Math.round(averageRating * 10.0) / 10.0); // Format to one decimal place

        // --- Services and Vehicle Types Mapping ---
        List<ParticularGarageService> garageServices = particularGarageServiceRepository.findByGarageId(garage.getId());

        // Create a list of service tags (e.g., "General Service" -> "general-service")
        List<GarageCardDTO.ServiceTagDTO> serviceTags = garageServices.stream().map(pgs -> {
            GarageCardDTO.ServiceTagDTO tag = new GarageCardDTO.ServiceTagDTO();
            String serviceValue = pgs.getService().getName().toLowerCase().replace(" ", "-");
            tag.setValue(serviceValue);
            tag.setDisplayName(pgs.getService().getName());
            return tag;
        }).collect(Collectors.toList());
        dto.setServices(serviceTags);

        // Extract the unique vehicle types this garage caters to (e.g., "car", "bike")
        Set<String> vehicleTypes = new HashSet<>();
        garageServices.forEach(pgs -> vehicleTypes.add(pgs.getVehicleType().getName().toLowerCase()));
        dto.setVehicleTypes(vehicleTypes);

        return dto;
    }
}