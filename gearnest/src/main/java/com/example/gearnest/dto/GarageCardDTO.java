package com.example.gearnest.dto;

import java.util.List;
import java.util.Set;

/**
 * Data Transfer Object for sending structured garage data to the dashboard
 * view.
 * This acts as a clean and secure data container for the frontend.
 * Created on: 23 September 2025
 */
public class GarageCardDTO {

    private Long id;
    private String garageName;
    private String address;
    private String cardImage;
    private double averageRating;
    private CityDTO city;
    private List<ServiceTagDTO> services;
    private Set<String> vehicleTypes;
    private String profileImage;
    private String description;

    // --- Inner DTO for City Info ---
    public static class CityDTO {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    // --- Inner DTO for Service Tags ---
    public static class ServiceTagDTO {
        private String value; // A machine-readable value like "general-service"
        private String displayName; // A human-readable name like "General Service"

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }
    }

    // --- Getters and Setters for GarageCardDTO ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGarageName() {
        return garageName;
    }

    public void setGarageName(String garageName) {
        this.garageName = garageName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCardImage() {
        return cardImage;
    }

    public void setCardImage(String cardImage) {
        this.cardImage = cardImage;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public CityDTO getCity() {
        return city;
    }

    public void setCity(CityDTO city) {
        this.city = city;
    }

    public List<ServiceTagDTO> getServices() {
        return services;
    }

    public void setServices(List<ServiceTagDTO> services) {
        this.services = services;
    }

    public Set<String> getVehicleTypes() {
        return vehicleTypes;
    }

    public void setVehicleTypes(Set<String> vehicleTypes) {
        this.vehicleTypes = vehicleTypes;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}