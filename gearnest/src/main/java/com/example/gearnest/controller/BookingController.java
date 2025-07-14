package com.example.gearnest.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.gearnest.model.Booking;
import com.example.gearnest.model.Garage;
import com.example.gearnest.repository.BookingRepository;
import com.example.gearnest.repository.GarageProfileRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class BookingController {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private GarageProfileRepository garageRepository;

    @GetMapping("/book-service/{garageId}")
    public String showBookingForm(@PathVariable Long garageId, Model model, HttpSession session) {
        Garage garage = garageRepository.findById(garageId).orElseThrow(() -> new RuntimeException("Garage not found"));

        Booking booking = new Booking();
        booking.setGarageId(garage.getId()); // pre-fill garageId

        model.addAttribute("garage", garage);
        model.addAttribute("booking", booking);
        model.addAttribute("success", session.getAttribute("success"));
        model.addAttribute("error", session.getAttribute("error"));

        session.removeAttribute("success");
        session.removeAttribute("error");

        return "garage-booking"; // Must match HTML filename
    }

    @PostMapping("/book-service")
    public String bookService(@ModelAttribute Booking booking, HttpSession session) {
        booking.setCreatedAt(LocalDateTime.now());
        booking.setStatus("Approved");

        bookingRepository.save(booking);
        session.setAttribute("success", "Your booking has been submitted!");

        return "redirect:/garage/" + booking.getGarageId();
    }

    @GetMapping("/garage/bookings/{garageId}")
    public String viewGarageBookings(@PathVariable Long garageId, Model model) {
        List<Booking> bookings = bookingRepository.findByGarageId(garageId);
        model.addAttribute("bookings", bookings);
        model.addAttribute("garageId", garageId);
        return "garage-bookings";
    }

    @PostMapping("/garage/bookings/update")
    public String updateBooking(
            @RequestParam Long bookingId,
            @RequestParam String status,
            @RequestParam String bookingTime,
            HttpSession session) {

        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if (booking != null) {
            booking.setStatus(status);
            booking.setBookingTime(bookingTime);
            bookingRepository.save(booking);
            session.setAttribute("success", "Booking updated!");
        }

        return "redirect:/garage/bookings/" + (booking != null ? booking.getGarageId() : "");
    }

}
