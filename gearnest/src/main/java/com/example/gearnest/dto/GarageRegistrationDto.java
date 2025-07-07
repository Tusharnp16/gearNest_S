package com.example.gearnest.dto;

import lombok.Data;

@Data
public class GarageRegistrationDto {
    private String ownerName;
    private String email;
    private String password;
    private String garageName;
    private String address;
    private String contact;
    private String description;
}
