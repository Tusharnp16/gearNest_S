package com.example.gearnest.dto;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import com.example.gearnest.model.Booking;
import com.example.gearnest.model.User;

public class InvoiceDetailsDTO {
    private String id;
    private String garageName;
    private String garageAddress;
    private String garageCity;
    private String garageEmail;
    private UserDetailsDTO user;
    private String vehicleMake;
    private String vehicleModel;
    private String vehicleNumber;
    private String pickupAddress;
    private String bookingDate;
    private String timeSlot;
    private double serviceSubtotal;
    private double taxes;
    private double convenienceFee = 50.00;
    private double totalAmount;
    private String status;
    private String serviceNames;
    private String invoiceDate;
    private String paymentMethod;
    private String transactionId;

    public InvoiceDetailsDTO(Booking booking) {
        this.id = booking.getId();
        this.garageName = booking.getGarage().getGarageName();
        this.garageAddress = booking.getGarage().getAddress();
        this.garageCity = booking.getGarage().getCity().getName() + ", " + booking.getGarage().getState().getName()
                + ", " + "395009";
        this.garageEmail = booking.getGarage().getEmail();
        this.user = new UserDetailsDTO(booking.getUser());
        this.vehicleMake = booking.getVehicleMake();
        this.vehicleModel = booking.getVehicleModel();
        this.vehicleNumber = booking.getVehicleNumber();
        this.pickupAddress = booking.getPickupAddress();
        this.bookingDate = booking.getBookingDate().toLocalDate().toString();
        this.timeSlot = booking.getTimeSlot();
        this.totalAmount = booking.getTotalFee();
        this.status = booking.getStatus();
        this.serviceNames = booking.getBookingServiceItems().stream()
                .map(item -> item.getParticularGarageService().getService().getName())
                .collect(Collectors.joining(", "));

        // Calculate subtotal and taxes from the total amount
        double feeAndTaxMultiplier = 1.05; // 5% tax
        this.serviceSubtotal = (this.totalAmount - this.convenienceFee) / feeAndTaxMultiplier;
        this.taxes = this.serviceSubtotal * 0.05;

        this.invoiceDate = LocalDateTime.now().toLocalDate().toString();
        this.paymentMethod = booking.getRazorpayPaymentId() != null ? "Online" : "Pay at Garage";
        this.transactionId = booking.getRazorpayOrderId();
    }

    // Nested DTO for user details
    public static class UserDetailsDTO {
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
        private String address;

        public UserDetailsDTO(User user) {
            this.firstName = user.getFirstName();
            this.lastName = user.getLastName();
            this.email = user.getEmail();
            this.phone = user.getPhone();
            this.address = user.getAddress();
        }

        // Getters and Setters
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

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }
    }

    // Getters and Setters for InvoiceDetailsDTO
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

    public String getGarageAddress() {
        return garageAddress;
    }

    public void setGarageAddress(String garageAddress) {
        this.garageAddress = garageAddress;
    }

    public String getGarageCity() {
        return garageCity;
    }

    public void setGarageCity(String garageCity) {
        this.garageCity = garageCity;
    }

    public String getGarageEmail() {
        return garageEmail;
    }

    public void setGarageEmail(String garageEmail) {
        this.garageEmail = garageEmail;
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

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public double getServiceSubtotal() {
        return serviceSubtotal;
    }

    public void setServiceSubtotal(double serviceSubtotal) {
        this.serviceSubtotal = serviceSubtotal;
    }

    public double getTaxes() {
        return taxes;
    }

    public void setTaxes(double taxes) {
        this.taxes = taxes;
    }

    public double getConvenienceFee() {
        return convenienceFee;
    }

    public void setConvenienceFee(double convenienceFee) {
        this.convenienceFee = convenienceFee;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getServiceNames() {
        return serviceNames;
    }

    public void setServiceNames(String serviceNames) {
        this.serviceNames = serviceNames;
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
}
