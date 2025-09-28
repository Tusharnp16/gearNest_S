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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.gearnest.model.Booking;
import com.example.gearnest.model.User;
import com.example.gearnest.repository.BookingRepository;
import com.example.gearnest.repository.UserRepository;
import com.example.gearnest.services.EmailService;
import com.example.gearnest.services.OtpService;

import jakarta.mail.MessagingException;

@Controller
@RequestMapping("/user")
public class myBookingList {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private OtpService otpService;

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
        model.addAttribute("activePage", "my-bookings");

        return "myBookings";
    }

    @PostMapping("/send-otp")
    @ResponseBody
    public ResponseEntity<String> sendOtpForCancellation(@RequestParam String bookingId, Principal principal) {
        Optional<Booking> bookingOptional = bookingRepository.findById(bookingId);
        if (bookingOptional.isEmpty()) {
            return new ResponseEntity<>("Booking not found.", HttpStatus.NOT_FOUND);
        }

        Booking booking = bookingOptional.get();
        if (!booking.getUser().getEmail().equals(principal.getName())) {
            return new ResponseEntity<>("Unauthorized to cancel this booking.", HttpStatus.FORBIDDEN);
        }
        if (booking.getStatus().equalsIgnoreCase("Cancelled") || booking.getStatus().equalsIgnoreCase("Completed")) {
            return new ResponseEntity<>("Cannot cancel a completed or already cancelled booking.",
                    HttpStatus.BAD_REQUEST);
        }

        try {
            User user = booking.getUser();
            String otp = otpService.generateOtp();
            user.setOtp(otp);
            userRepository.save(user);

            emailService.sendOtpEmail(user, otp);
            return new ResponseEntity<>("OTP sent to your email.", HttpStatus.OK);
        } catch (MessagingException e) {
            return new ResponseEntity<>("Failed to send OTP email.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/cancel-booking-with-otp")
    @ResponseBody
    public ResponseEntity<String> cancelBookingWithOtp(@RequestParam String bookingId, @RequestParam String otp,
            Principal principal) {
        Optional<Booking> bookingOptional = bookingRepository.findById(bookingId);
        if (bookingOptional.isEmpty()) {
            return new ResponseEntity<>("Booking not found.", HttpStatus.NOT_FOUND);
        }

        Booking booking = bookingOptional.get();
        User user = booking.getUser();

        if (!user.getEmail().equals(principal.getName())) {
            return new ResponseEntity<>("Unauthorized to cancel this booking.", HttpStatus.FORBIDDEN);
        }

        if (user.getOtp() == null || !user.getOtp().equals(otp)) {
            return new ResponseEntity<>("Invalid OTP.", HttpStatus.UNAUTHORIZED);
        }

        booking.setStatus("Cancelled");
        bookingRepository.save(booking);
        user.setOtp(null); // Clear the OTP after use
        userRepository.save(user);

        try {
            emailService.sendBookingCancellationEmail(user, booking);
        } catch (MessagingException e) {
            System.err.println("Failed to send cancellation email: " + e.getMessage());
        }

        return new ResponseEntity<>("Booking has been successfully cancelled.", HttpStatus.OK);
    }
}
