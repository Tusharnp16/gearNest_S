package com.example.gearnest.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BookService {

    @GetMapping("bookservice")
    public String bookservice() {
        return "bookservice";
    }

}
