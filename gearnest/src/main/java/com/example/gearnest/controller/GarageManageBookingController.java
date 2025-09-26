package com.example.gearnest.controller;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.gearnest.dto.BookingDetailsDTO;
import com.example.gearnest.model.Booking;
import com.example.gearnest.model.Garage;
import com.example.gearnest.model.User;
import com.example.gearnest.repository.BookingRepository;
import com.example.gearnest.repository.GarageRepository;
import com.example.gearnest.services.EmailService;

import jakarta.mail.MessagingException;

@Controller
@RequestMapping("/garage")
public class GarageManageBookingController {

    @Autowired
    private GarageRepository garageRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private EmailService emailService;

    @GetMapping("/bookings")
    public String manageBookings(Principal principal, Model model, RedirectAttributes redirectAttributes) {
        if (principal == null) {
            redirectAttributes.addFlashAttribute("error", "You must be logged in as a garage owner to view this page.");
            return "redirect:/login";
        }

        Garage garage = garageRepository.findByEmail(principal.getName());
        if (garage == null) {
            redirectAttributes.addFlashAttribute("error", "Garage not found for this user.");
            return "redirect:/dashboard";
        }

        List<Booking> bookings = bookingRepository.findByGarageId(garage.getId());

        model.addAttribute("title", "Manage Bookings");
        model.addAttribute("bookings", bookings);
        model.addAttribute("garage", garage);
        model.addAttribute("activePage", "bookings");

        return "garage/manage_bookings";
    }

    @GetMapping("/booking/{id}")
    @ResponseBody
    public ResponseEntity<BookingDetailsDTO> getBookingDetails(@PathVariable String id) {
        Optional<Booking> bookingOptional = bookingRepository.findById(id);
        if (bookingOptional.isPresent()) {
            Booking booking = bookingOptional.get();
            // Map the Booking entity to the DTO
            BookingDetailsDTO dto = new BookingDetailsDTO(booking);
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/booking/update-status")
    @ResponseBody
    public ResponseEntity<String> updateBookingStatus(@RequestParam String bookingId, @RequestParam String status) {
        Optional<Booking> bookingOptional = bookingRepository.findById(bookingId);
        if (bookingOptional.isPresent()) {
            Booking booking = bookingOptional.get();
            booking.setStatus(status);
            bookingRepository.save(booking);

            // Fetch the user to send the email
            User user = booking.getUser();
            if (user != null) {
                try {
                    emailService.sendBookingStatusUpdateEmail(user, booking);
                } catch (MessagingException e) {
                    System.err.println("Failed to send status update email: " + e.getMessage());
                }
            }

            return new ResponseEntity<>("Booking status updated successfully!", HttpStatus.OK);
        }
        return new ResponseEntity<>("Booking not found.", HttpStatus.NOT_FOUND);
    }
}
