package com.example.gearnest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.gearnest.repository.BookingRepository;

@Controller
@RequestMapping("/admin/bookings")
public class AdminBookingController {

    @Autowired
    private BookingRepository bookingRepository;

    @GetMapping
    public String bookings(Model model) {
        model.addAttribute("title", "-Bookings");
        model.addAttribute("activePage", "bookings");
        model.addAttribute("bookingList", bookingRepository.findAll());

        return "admin/admin-booking-list";
    }
}