package com.example.gearnest.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.example.gearnest.model.Booking;
import com.example.gearnest.model.User;
import com.fasterxml.jackson.annotation.JsonFormat;

public class BookingDetailsDTO {
    private String id;
    private String garageName;
    private UserDetailsDTO user;
    private String vehicleMake;
    private String vehicleModel;
    private String vehicleNumber;
    private String pickupAddress;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime bookingDate;
    private String timeSlot;
    private double totalFee;
    private String status;
    private List<String> serviceNames;

    public BookingDetailsDTO(Booking booking) {
        this.id = booking.getId();
        this.garageName = booking.getGarage().getGarageName();
        this.user = new UserDetailsDTO(booking.getUser());
        this.vehicleMake = booking.getVehicleMake();
        this.vehicleModel = booking.getVehicleModel();
        this.vehicleNumber = booking.getVehicleNumber();
        this.pickupAddress = booking.getPickupAddress();
        this.bookingDate = booking.getBookingDate();
        this.timeSlot = booking.getTimeSlot();
        this.totalFee = booking.getTotalFee();
        this.status = booking.getStatus();
        this.serviceNames = booking.getBookingServiceItems().stream()
                .map(item -> item.getParticularGarageService().getService().getName())
                .collect(Collectors.toList());
    }

    // Nested DTO for user details to prevent recursion
    public static class UserDetailsDTO {
        private Long id;
        private String firstName;
        private String lastName;
        private String email;
        private String phone;

        public UserDetailsDTO(User user) {
            this.id = user.getId();
            this.firstName = user.getFirstName();
            this.lastName = user.getLastName();
            this.email = user.getEmail();
            this.phone = user.getPhone();
        }

        // Getters and Setters for UserDetailsDTO
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }
    }

    // Getters and Setters for BookingDetailsDTO
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGarageName() {
        return garageName;
    }

    public void setGarageName(String garageName) {
        this.garageName = garageName;
    }

    public UserDetailsDTO getUser() {
        return user;
    }

    public void setUser(UserDetailsDTO user) {
        this.user = user;
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

    public List<String> getServiceNames() {
        return serviceNames;
    }

    public void setServiceNames(List<String> serviceNames) {
        this.serviceNames = serviceNames;
    }
}
