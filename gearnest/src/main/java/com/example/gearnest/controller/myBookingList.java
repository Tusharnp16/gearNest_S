package com.example.gearnest.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.gearnest.model.Booking;
import com.example.gearnest.model.User;
import com.example.gearnest.repository.BookingRepository;
import com.example.gearnest.repository.UserRepository;

@Controller
@RequestMapping("/user")
public class myBookingList {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @GetMapping("/my-bookings")
    public String myBookings(Principal principal, Model model, RedirectAttributes redirectAttributes) {
        if (principal == null) {
            redirectAttributes.addFlashAttribute("error", "You must be logged in to view your bookings.");
            return "redirect:/login";
        }

        User user = userRepository.findByEmail(principal.getName());
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "User not found.");
            return "redirect:/dashboard";
        }

        List<Booking> bookings = bookingRepository.findByUserId(user.getId());

        model.addAttribute("title", "My Bookings");
        model.addAttribute("bookings", bookings);

        return "myBookings";
    }
}
