package com.example.gearnest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.gearnest.model.Feedback;
import com.example.gearnest.repository.FeedbackRepository;

@Controller
public class FeedbackController {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @GetMapping("/user/feedback")
    public String showFeedbackForm(Model model) {
        model.addAttribute("feedback", new Feedback());
        return "feedback";
    }

    @PostMapping("/user/feedback")
    public String submitFeedback(@ModelAttribute Feedback feedback, Model model) {
        feedbackRepository.save(feedback);
        model.addAttribute("success", "Thanks for your valuable feedback!");
        model.addAttribute("feedback", new Feedback());
        return "feedback";
    }
}
