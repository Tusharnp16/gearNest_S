package com.example.gearnest.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;

@Entity
public class Booking {

        @Id
        private String id;

        @ManyToOne
        @JoinColumn(name = "garage_id", nullable = false)
        private Garage garage;

        @ManyToOne
        @JoinColumn(name = "user_id", nullable = false)
        private User user;

        @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<BookingServiceItem> bookingServiceItems = new ArrayList<>();

        private String vehicleMake;
        private String vehicleModel;
        private String vehicleNumber;
        private String pickupAddress;
        private LocalDateTime bookingDate;
        private String timeSlot;
        private double totalFee;
        private String status;

        private String razorpayPaymentId;
        private String razorpayOrderId;
        private String razorpaySignature;

        @PrePersist
        protected void onCreate() {
                this.bookingDate = LocalDateTime.now();
        }

        // --- Getters and Setters ---
        public String getId() {
                return id;
        }

        public void setId(String id) {
                this.id = id;
        }

        public Garage getGarage() {
                return garage;
        }

        public void setGarage(Garage garage) {
                this.garage = garage;
        }

        public User getUser() {
                return user;
        }

        public void setUser(User user) {
                this.user = user;
        }

        public List<BookingServiceItem> getBookingServiceItems() {
                return bookingServiceItems;
        }

        public void setBookingServiceItems(List<BookingServiceItem> bookingServiceItems) {
                this.bookingServiceItems = bookingServiceItems;
        }

        public String getVehicleMake() {
                return vehicleMake;
        }

        public void setVehicleMake(String vehicleMake) {
                this.vehicleMake = vehicleMake;
        }

        public String getVehicleModel() {
                return vehicleModel;
        }

        public void setVehicleModel(String vehicleModel) {
                this.vehicleModel = vehicleModel;
        }

        public String getVehicleNumber() {
                return vehicleNumber;
        }

        public void setVehicleNumber(String vehicleNumber) {
                this.vehicleNumber = vehicleNumber;
        }

        public String getPickupAddress() {
                return pickupAddress;
        }

        public void setPickupAddress(String pickupAddress) {
                this.pickupAddress = pickupAddress;
        }

        public LocalDateTime getBookingDate() {
                return bookingDate;
        }

        public void setBookingDate(LocalDateTime bookingDate) {
                this.bookingDate = bookingDate;
        }

        public String getTimeSlot() {
                return timeSlot;
        }

        public void setTimeSlot(String timeSlot) {
                this.timeSlot = timeSlot;
        }

        public double getTotalFee() {
                return totalFee;
        }

        public void setTotalFee(double totalFee) {
                this.totalFee = totalFee;
        }

        public String getStatus() {
                return status;
        }

        public void setStatus(String status) {
                this.status = status;
        }

        public String getRazorpayPaymentId() {
                return razorpayPaymentId;
        }

        public void setRazorpayPaymentId(String razorpayPaymentId) {
                this.razorpayPaymentId = razorpayPaymentId;
        }

        public String getRazorpayOrderId() {
                return razorpayOrderId;
        }

        public void setRazorpayOrderId(String razorpayOrderId) {
                this.razorpayOrderId = razorpayOrderId;
        }

        public String getRazorpaySignature() {
                return razorpaySignature;
        }

        public void setRazorpaySignature(String razorpaySignature) {
                this.razorpaySignature = razorpaySignature;
        }
}
