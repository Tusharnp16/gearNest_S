package com.example.gearnest.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class myBookingList {

    @GetMapping("/user/my-bookings")
    public String myBookings() {
        return "myBookings";
    }
}
