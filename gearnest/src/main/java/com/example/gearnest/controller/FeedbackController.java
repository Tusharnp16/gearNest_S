package com.example.gearnest.controller;

import java.security.Principal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.gearnest.model.Booking;
import com.example.gearnest.model.Feedback;
import com.example.gearnest.model.User;
import com.example.gearnest.repository.BookingRepository;
import com.example.gearnest.repository.FeedbackRepository;
import com.example.gearnest.repository.UserRepository;

@Controller
@RequestMapping("/user")
public class FeedbackController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    String bookingId;

    @GetMapping("/feedback")
    public String showFeedbackForm(@RequestParam("bookingId") String bookingId, Principal principal, Model model,
            RedirectAttributes redirectAttributes) {
        this.bookingId = bookingId;
        if (principal == null) {
            redirectAttributes.addFlashAttribute("error", "You must be logged in to leave feedback.");
            return "redirect:/login";
        }

        Optional<Booking> bookingOptional = bookingRepository.findById(bookingId);
        if (bookingOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Booking not found.");
            return "redirect:/user/my-bookings";
        }

        Booking booking = bookingOptional.get();
        User loggedInUser = userRepository.findByEmail(principal.getName());

        if (loggedInUser == null || !booking.getUser().getId().equals(loggedInUser.getId())) {
            redirectAttributes.addFlashAttribute("error",
                    "You do not have permission to leave feedback for this booking.");
            return "redirect:/user/my-bookings";
        }

        // Pre-populate the Feedback model with user and garage data
        Feedback feedback = new Feedback();
        feedback.setUser(loggedInUser);
        feedback.setGarage(booking.getGarage());

        model.addAttribute("title", "Rate & Review");
        model.addAttribute("booking", booking);
        model.addAttribute("feedback", feedback);

        return "feedback";
    }

    @PostMapping("/feedback/submit")
    public String submitFeedback(@ModelAttribute Feedback feedback, Principal principal,
            RedirectAttributes redirectAttributes) {
        if (principal == null) {
            redirectAttributes.addFlashAttribute("error", "You must be logged in to submit feedback.");
            return "redirect:/login";
        }

        User loggedInUser = userRepository.findByEmail(principal.getName());

        // Basic security validation
        if (loggedInUser == null || !feedback.getUser().getId().equals(loggedInUser.getId())) {
            redirectAttributes.addFlashAttribute("error", "Invalid user or unauthorized submission.");
            return "redirect:/user/my-bookings";
        }

        // The garage field is already populated by the Hidden field in the form.
        // We only check if the garage ID is valid.
        if (feedback.getGarage() == null || feedback.getGarage().getId() == null) {
            redirectAttributes.addFlashAttribute("error", "Garage information is missing.");
            return "redirect:/user/my-bookings";
        }

        feedback.setReviewed(true); // Mark as reviewed
        feedbackRepository.save(feedback);

        redirectAttributes.addFlashAttribute("success", "Thank you for your feedback!");
        return "redirect:/user/feedback?bookingId=" + bookingId;
    }
}
