package com.example.gearnest.controller;

import java.security.Principal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.gearnest.dto.InvoiceDetailsDTO;
import com.example.gearnest.model.Booking;
import com.example.gearnest.model.User;
import com.example.gearnest.repository.BookingRepository;
import com.example.gearnest.repository.UserRepository;

@Controller
@RequestMapping("/user")
public class InvoiceController {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/invoice/{id}")
    public String viewInvoice(@PathVariable String id, Principal principal, Model model,
            RedirectAttributes redirectAttributes) {
        if (principal == null) {
            redirectAttributes.addFlashAttribute("error", "You must be logged in to view this invoice.");
            return "redirect:/login";
        }

        Optional<Booking> bookingOptional = bookingRepository.findById(id);
        if (bookingOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Invoice not found.");
            return "redirect:/dashboard";
        }

        Booking booking = bookingOptional.get();

        User loggedInUser = userRepository.findByEmail(principal.getName());

        // Security check: ensure the logged-in user owns this booking
        if (loggedInUser == null || !booking.getUser().getId().equals(loggedInUser.getId())) {
            redirectAttributes.addFlashAttribute("error", "You do not have permission to view this invoice.");
            return "redirect:/user/my-bookings";
        }

        // Use a DTO to prevent serialization errors
        InvoiceDetailsDTO invoiceDetails = new InvoiceDetailsDTO(booking);

        model.addAttribute("booking", invoiceDetails);
        model.addAttribute("title", "Invoice - " + invoiceDetails.getId());

        return "invoice";
    }
}
