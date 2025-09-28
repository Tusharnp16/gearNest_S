package com.example.gearnest.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import com.example.gearnest.model.Booking;
import com.example.gearnest.model.Garage;
import com.example.gearnest.model.User;
import com.example.gearnest.repository.BookingRepository;
import com.example.gearnest.repository.GarageProfileRepository;

/**
 * Controller dedicated to managing customer data specific to the garage panel.
 */
@Controller
@RequestMapping("/garage")
public class GarageCustomersController {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private GarageProfileRepository garageRepository;

    // ------------------------------------------------------------------
    // DTO to combine User details and Booking count for the customers table
    // ------------------------------------------------------------------
    /**
     * Data Transfer Object (DTO) to hold all customer details needed by the view.
     */
    public record CustomerDetailDto(
            Long id,
            String profilePicture, // New: Profile picture filename
            String fullName,
            String email,
            String phoneNumber,
            String city,
            String state,
            long totalBookings) {
    }

    /**
     * Handler for the /garage/customers endpoint.
     * Fetches all unique customers who have booked a service at the current garage
     * and calculates their total booking count.
     */
    @GetMapping("/customers") // Mapped to /garage/customers
    public String viewAllCustomersForGarage(Model model) {

        // TODO: REPLACE THIS MOCK ID LOGIC WITH ACTUAL AUTHENTICATION LOGIC (e.g., from
        // SecurityContext)
        Long currentGarageId = 1L; // MOCK ID - Replace with real logic

        // 1. Fetch the Garage object (Required by the base template)
        Garage garage = garageRepository.findById(currentGarageId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Garage not found with ID: " + currentGarageId));

        model.addAttribute("garage", garage);

        // 2. Fetch all bookings for the current garage
        List<Booking> garageBookings = bookingRepository.findByGarageId(currentGarageId);

        // 3. Group bookings by the customer (User)
        Map<User, Long> userBookingCounts = garageBookings.stream()
                .filter(booking -> booking.getUser() != null)
                .collect(Collectors.groupingBy(
                        Booking::getUser,
                        Collectors.counting()));

        // 4. Convert the map entries into a List of CustomerDetailDto
        List<CustomerDetailDto> customerDetails = userBookingCounts.entrySet().stream()
                .map(entry -> {
                    User user = entry.getKey();
                    long count = entry.getValue();

                    // Safely extract names for relationships
                    String cityName = (user.getCity() != null) ? user.getCity().getName() : "N/A";
                    String stateName = (user.getState() != null) ? user.getState().getName() : "N/A";

                    return new CustomerDetailDto(
                            user.getId(),
                            user.getProfilePic(), // Used for image display
                            user.getFirstName() + " " + user.getLastName(),
                            user.getEmail(),
                            user.getPhone(),
                            cityName,
                            stateName,
                            count);
                })
                .collect(Collectors.toList());

        model.addAttribute("title", "Customers");
        model.addAttribute("activePage", "customers");
        model.addAttribute("customers", customerDetails); // Data for the HTML table

        return "garage/garage-customers";
    }
}
