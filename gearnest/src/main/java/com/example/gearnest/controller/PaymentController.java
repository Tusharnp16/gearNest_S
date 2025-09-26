package com.example.gearnest.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.gearnest.dto.BookingDTO;
import com.example.gearnest.model.Booking;
import com.example.gearnest.model.BookingServiceItem;
import com.example.gearnest.model.Garage;
import com.example.gearnest.model.ParticularGarageService;
import com.example.gearnest.model.User;
import com.example.gearnest.repository.BookingRepository;
import com.example.gearnest.repository.GarageRepository;
import com.example.gearnest.repository.ParticularGarageServiceRepository;
import com.example.gearnest.repository.UserRepository;
import com.example.gearnest.services.EmailService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

@RestController
@RequestMapping("/user")
public class PaymentController {

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private GarageRepository garageRepository;
    @Autowired
    private ParticularGarageServiceRepository particularGarageServiceRepository;
    @Autowired
    private EmailService emailService;

    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestBody BookingDTO bookingDTO, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated.");
        }
        try {
            RazorpayClient razorpayClient = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", bookingDTO.getTotalFee() * 100);
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "gear_nest_booking_" + System.currentTimeMillis());

            Order order = razorpayClient.orders.create(orderRequest);
            return ResponseEntity.ok(order.toString());
        } catch (RazorpayException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating Razorpay order: " + e.getMessage());
        }
    }

    @PostMapping("/verify-payment")
    public ResponseEntity<?> verifyPayment(@RequestBody BookingDTO bookingDTO, Principal principal) {
        User user = userRepository.findByEmail(principal.getName());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found.");
        }

        try {
            Optional<Garage> garageOptional = garageRepository.findById(bookingDTO.getGarageId());
            if (garageOptional.isEmpty()) {
                return ResponseEntity.badRequest().body("Garage not found.");
            }
            Garage garage = garageOptional.get();

            String bookingIdFormat = "GNS" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyyyyHHmm"));

            Booking booking = new Booking();
            booking.setId(bookingIdFormat);
            booking.setGarage(garage);
            booking.setUser(user);
            booking.setVehicleMake(bookingDTO.getVehicleMake());
            booking.setVehicleModel(bookingDTO.getVehicleModel());
            booking.setVehicleNumber(bookingDTO.getVehicleNumber());
            booking.setPickupAddress(bookingDTO.getPickupAddress());
            booking.setTimeSlot(bookingDTO.getTimeSlot());
            booking.setTotalFee(bookingDTO.getTotalFee());
            booking.setStatus("Booked");
            booking.setBookingDate(LocalDateTime.now());
            booking.setRazorpayPaymentId(bookingDTO.getRazorpayPaymentId());
            booking.setRazorpayOrderId(bookingDTO.getRazorpayOrderId());
            booking.setRazorpaySignature(bookingDTO.getRazorpaySignature());

            for (Long serviceId : bookingDTO.getParticularGarageServiceIds()) {
                Optional<ParticularGarageService> serviceOptional = particularGarageServiceRepository
                        .findById(serviceId);
                if (serviceOptional.isPresent()) {
                    BookingServiceItem item = new BookingServiceItem();
                    item.setBooking(booking);
                    item.setParticularGarageService(serviceOptional.get());
                    booking.getBookingServiceItems().add(item);
                }
            }

            bookingRepository.save(booking);

            emailService.sendBookingConfirmationEmail(user, booking);

            return ResponseEntity.ok("Payment verified and bookings saved successfully!");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error saving booking: " + e.getMessage());
        }
    }
}
