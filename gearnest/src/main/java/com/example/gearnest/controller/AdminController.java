package com.example.gearnest.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.gearnest.model.Garage;
import com.example.gearnest.repository.BookingRepository;
import com.example.gearnest.repository.GarageProfileRepository;
import com.example.gearnest.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class AdminController {

    @Autowired
    private GarageProfileRepository garageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @GetMapping("/admin/dashboard")
    public String Dashboard(Model model) {
        model.addAttribute("title", " ");

        model.addAttribute("activePage", "dashboard");

        long userCount = userRepository.count(); // Counts all users
        model.addAttribute("userCount", userCount);
        long garageCount = garageRepository.count();// Counts all garages
        model.addAttribute("garageCount", garageCount);

        long bookingCount = bookingRepository.count();
        model.addAttribute("bookingCount", bookingCount);
        return "admin/admin-dashboard";
    }

    @GetMapping("/admin/garages")
    public String viewAllGaragesForAdmin(Model model) {
        List<Garage> garages = garageRepository.findAll();
        model.addAttribute("title", "-Garages");
        model.addAttribute("activePage", "garages");
        model.addAttribute("garages", garages);
        return "admin/admin-garages";
    }

    @PostMapping("/admin/garage/{id}/update-status")
    public String updateGarageStatus(@PathVariable Long id,
            @RequestParam("status") String status,
            HttpSession session) {
        Garage garage = garageRepository.findById(id).orElse(null);
        if (garage != null) {
            garage.setStatus(status);
            garage.setApproved("Active".equals(status));
            garageRepository.save(garage);
            session.setAttribute("success", "Garage status updated.");
        } else {
            session.setAttribute("error", "Garage not found.");
        }
        return "redirect:/admin/garages";
    }

}
