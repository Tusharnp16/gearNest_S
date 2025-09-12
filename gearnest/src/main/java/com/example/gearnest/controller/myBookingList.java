package com.example.gearnest.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class myBookingList {

    @GetMapping("/mybookings")
    public String myBookings() {
        return "myBookings";
    }
}
