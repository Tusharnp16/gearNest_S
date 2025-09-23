package com.example.gearnest.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Invoice {
    @GetMapping("/user/invoice")
    public String getinvoice() {
        return "invoice";
    }

}
