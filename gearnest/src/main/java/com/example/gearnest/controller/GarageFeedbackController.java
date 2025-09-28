package com.example.gearnest.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.gearnest.model.Feedback;
import com.example.gearnest.model.Garage;
import com.example.gearnest.repository.FeedbackRepository;
import com.example.gearnest.repository.GarageRepository;

@Controller
@RequestMapping("/garage")
public class GarageFeedbackController {

    @Autowired
    private GarageRepository garageRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @GetMapping("/feedback")
    public String manageFeedback(Principal principal, Model model, RedirectAttributes redirectAttributes) {
        if (principal == null) {
            redirectAttributes.addFlashAttribute("error", "You must be logged in as a garage owner to view feedback.");
            return "redirect:/login";
        }

        Garage loggedInGarage = garageRepository.findByEmail(principal.getName());
        if (loggedInGarage == null) {
            redirectAttributes.addFlashAttribute("error", "Garage not found for this user.");
            return "redirect:/dashboard";
        }

        // Fetch all feedback for the logged-in garage
        List<Feedback> feedbackList = feedbackRepository.findByGarageId(loggedInGarage.getId());

        model.addAttribute("title", "Manage Feedback");
        model.addAttribute("feedbackList", feedbackList);
        model.addAttribute("garage", loggedInGarage);
        model.addAttribute("activePage", "feedback"); // For sidebar highlighting

        return "garage/garage_feedback";
    }
}
