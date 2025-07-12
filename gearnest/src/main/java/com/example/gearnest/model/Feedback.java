package com.example.gearnest.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private Integer rating; 

    @Column(length = 1000)
    private String message;

    private LocalDateTime createdAt;
    private boolean reviewed = false;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
