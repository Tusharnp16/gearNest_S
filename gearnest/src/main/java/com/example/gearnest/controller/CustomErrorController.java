package com.example.gearnest.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {

        // Get HTTP status code
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        // Get error reason (e.g., "Not Found")
        Object error = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        // Get exception trace
        Object exception = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);

        // Add attributes to model for Thymeleaf
        model.addAttribute("status", status != null ? status.toString() : "???");
        model.addAttribute("error", error != null ? error.toString() : "An Unexpected Error Occurred");
        if (exception != null) {
            model.addAttribute("trace", exception.toString());
        }

        return "error"; // Thymeleaf template
    }

    public String getErrorPath() {
        return "/error";
    }
}
