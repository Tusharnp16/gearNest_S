package com.example.gearnest.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final Map<String, String> faq = Map.of(
            "services", "We provide car repair, servicing, washing, and spare parts replacement.",
            "working hours", "Our garage is open from 9 AM to 8 PM, Mon-Sat.",
            "book service", "You can book a service by typing: book service <date> <car_model>."
    );

    @PostMapping
    public ResponseEntity<String> chat(@RequestBody String userMessage) {
        String msg = userMessage.toLowerCase();

        for (String key : faq.keySet()) {
            if (msg.contains(key)) {
                return ResponseEntity.ok(faq.get(key));
            }
        }

        if (msg.startsWith("book service")) {
            return ResponseEntity.ok("✅ Service booked successfully!");
        }

        return ResponseEntity.ok("Sorry, I didn’t understand that. Please try again.");
    }
}
 