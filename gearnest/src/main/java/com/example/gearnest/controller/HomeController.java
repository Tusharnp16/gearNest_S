package com.example.gearnest.controller;

import java.security.Principal;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.gearnest.dto.GarageCardDTO;
import com.example.gearnest.model.CarouselImage;
import com.example.gearnest.repository.CarouselImageRepository;
import com.example.gearnest.services.GarageService;

/**
 * Controller for handling home and dashboard-related requests.
 * This controller serves both public-facing pages and authenticated user pages.
 */
@Controller
public class HomeController {

    @Autowired
    private GarageService garageService;

    @Autowired
    private CarouselImageRepository carouselImageRepository;

    @GetMapping("/")
    public String rootRedirect(Principal principal, Model model) {
        addCommonAttributes(model);
        if (principal != null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                return "redirect:/admin/dashboard";
            }
            if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_GARAGE"))) {
                return "redirect:/garage/services";
            }
            if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER"))) {
                return "redirect:/user/dashboard";
            }
        }
        return "dashboard";
    }

    /**
     * Handles requests for the public and user dashboard.
     */
    @GetMapping({ "/dashboard", "/user/dashboard" })
    public String dashboard(Model model) {
        addCommonAttributes(model);
        return "dashboard";
    }

    /**
     * A helper method to add common data needed by all dashboard views.
     */
    private void addCommonAttributes(Model model) {
        // Use the service to get the list of DTOs.
        List<GarageCardDTO> garages = garageService.getGarageCardsForDashboard();
        model.addAttribute("garages", garages);

        // Logic to select a random hero image for the top of the page.
        List<CarouselImage> images = carouselImageRepository.findAll();
        String heroImageUrl = "/images/default-hero.jpg"; // A default fallback image

        if (!images.isEmpty()) {
            Random rand = new Random();
            CarouselImage randomImage = images.get(rand.nextInt(images.size()));
            heroImageUrl = "/uploads/carousel-images/" + randomImage.getFilename() + "?v=" + System.currentTimeMillis();
        }
        model.addAttribute("heroImageUrl", heroImageUrl);
    }
}
