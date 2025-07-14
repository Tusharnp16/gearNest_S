package com.example.gearnest.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long garageId; 
    private Long userId; 

    private String customerName;
    private String customerPhone;

    private String selectedService;
    private String bookingDate;
    private String bookingTime; 

    private String status="Approved"; 
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null || this.status.isBlank()) {
            this.status = "Approved";
        }
    }
}
