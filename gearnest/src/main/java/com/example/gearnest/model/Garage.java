package com.example.gearnest.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import lombok.*;

@Data
@Entity
public class Garage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String garageName;
    private String ownerName;
    private String email;
    private String phoneNumber;

    private String address;
    private String city;
    private String state;

    private String location; 
    private Double latitude;
    private Double longitude;

    private String logoPath;
    private String price;

    @Column(length = 1000)
    private String description;

    private boolean isApproved=false;

    private String openingHours;

    private String status="pending";
 
    private double rating = 5.0; 

    private LocalDateTime createdAt;

    private String password;


    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

  //  @ManyToOne
  //  @JoinColumn(nullable = true)
  //  private User user;

    @ElementCollection
    private List<String> servicesOffered;

}
