package com.example.gearnest.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;

@Entity
public class Garage {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String garageName;
  private String ownerName;
  private String email;
  private String phno;

  private String address;

  @ManyToOne
  private Cities city;

  @ManyToOne
  private States state;

  private String profileImage;

  // Field for the card display image
  private String cardImage;

  @Column(length = 1000)
  private String description;

  private boolean isApproved = false;
  private String openingHours;
  private String status = "pending";
  private LocalDateTime createdAt;

  private String password;
  private Boolean verified = false;
  private String otp;

  @PrePersist
  public void onCreate() {
    this.createdAt = LocalDateTime.now();
  }

  // --- Getters and Setters ---

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getGarageName() {
    return garageName;
  }

  public void setGarageName(String garageName) {
    this.garageName = garageName;
  }

  public String getOwnerName() {
    return ownerName;
  }

  public void setOwnerName(String ownerName) {
    this.ownerName = ownerName;
  }

  public String getPhno() {
    return phno;
  }

  public void setPhno(String phno) {
    this.phno = phno;
  }

  public String getProfileImage() {
    return profileImage;
  }

  public void setProfileImage(String profileImage) {
    this.profileImage = profileImage;
  }

  public String getCardImage() {
    return cardImage;
  }

  public void setCardImage(String cardImage) {
    this.cardImage = cardImage;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public Cities getCity() {
    return city;
  }

  public void setCity(Cities city) {
    this.city = city;
  }

  public States getState() {
    return state;
  }

  public void setState(States state) {
    this.state = state;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public boolean isApproved() {
    return isApproved;
  }

  public void setApproved(boolean isApproved) {
    this.isApproved = isApproved;
  }

  public String getOpeningHours() {
    return openingHours;
  }

  public void setOpeningHours(String openingHours) {
    this.openingHours = openingHours;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Boolean getVerified() {
    return verified;
  }

  public void setVerified(Boolean verified) {
    this.verified = verified;
  }

  public String getOtp() {
    return otp;
  }

  public void setOtp(String otp) {
    this.otp = otp;
  }
}
