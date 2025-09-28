package com.example.gearnest.services;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.example.gearnest.model.Booking;
import com.example.gearnest.model.User;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

        @Autowired
        private JavaMailSender mailSender;

        // Method to send a new booking confirmation email
        public void sendBookingConfirmationEmail(User user, Booking booking) throws MessagingException {
                MimeMessage mimeMessage = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

                helper.setFrom("noreply@gearnest.com");
                helper.setTo(user.getEmail());
                helper.setSubject("GearNest Booking Confirmation - Bo #" + booking.getId());

                String serviceNames = booking.getBookingServiceItems().stream()
                                .map(item -> item.getParticularGarageService().getService().getName())
                                .collect(Collectors.joining(", "));

                String htmlContent = String.format(
                                "<!DOCTYPE html>" +
                                                "<html>" +
                                                "<head>" +
                                                "    <style>" +
                                                "        body { font-family: 'Inter', sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; }"
                                                +
                                                "        .email-container { max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.05); overflow: hidden; }"
                                                +
                                                "        .header { background-color: #3498db; color: #ffffff; padding: 20px; text-align: center; }"
                                                +
                                                "        .content { padding: 20px; color: #333333; }" +
                                                "        .booking-details { border: 1px solid #e0e0e0; border-radius: 4px; padding: 15px; margin-top: 20px; }"
                                                +
                                                "        .detail-item { margin-bottom: 10px; }" +
                                                "        .detail-item span { font-weight: 600; color: #555555; }" +
                                                "        .footer { text-align: center; padding: 20px; font-size: 12px; color: #888888; }"
                                                +
                                                "    </style>" +
                                                "</head>" +
                                                "<body>" +
                                                "    <div class='email-container'>" +
                                                "        <div class='header'>" +
                                                "            <h1>Booking Confirmed!</h1>" +
                                                "        </div>" +
                                                "        <div class='content'>" +
                                                "            <p>Hello %s,</p>" +
                                                "            <p>Your service booking at <strong>%s</strong> has been successfully confirmed. We look forward to serving you!</p>"
                                                +
                                                "            <div class='booking-details'>" +
                                                "                <h3>Booking Details:</h3>" +
                                                "                <p class='detail-item'><span>Order ID:</span> %s</p>" +
                                                "                <p class='detail-item'><span>Vehicle:</span> %s %s (%s)</p>"
                                                +
                                                "                <p class='detail-item'><span>Services:</span> %s</p>" +
                                                "                <p class='detail-item'><span>Date:</span> %s</p>" +
                                                "                <p class='detail-item'><span>Time Slot:</span> %s</p>"
                                                +
                                                "                <p class='detail-item'><span>Total Fee:</span> ₹%.2f</p>"
                                                +
                                                "            </div>" +
                                                "            <p>Thank you for choosing GearNest!</p>" +
                                                "        </div>" +
                                                "        <div class='footer'>" +
                                                "            <p>&copy; %d GearNest. All rights reserved.</p>" +
                                                "        " +
                                                "</div>" +
                                                "</body>" +
                                                "</html>",
                                user.getFirstName(),
                                booking.getGarage().getGarageName(),
                                booking.getId(),
                                booking.getVehicleMake(),
                                booking.getVehicleModel(),
                                booking.getVehicleNumber(),
                                serviceNames,
                                booking.getBookingDate().toLocalDate(),
                                booking.getTimeSlot(),
                                booking.getTotalFee(),
                                LocalDateTime.now().getYear());

                helper.setText(htmlContent, true);
                mailSender.send(mimeMessage);
        }

        // Method to send a booking status update email
        public void sendBookingStatusUpdateEmail(User user, Booking booking) throws MessagingException {
                MimeMessage mimeMessage = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

                helper.setFrom("noreply@gearnest.com");
                helper.setTo(user.getEmail());
                helper.setSubject("GearNest Booking Status Updated - Order #" + booking.getId());

                String serviceNames = booking.getBookingServiceItems().stream()
                                .map(item -> item.getParticularGarageService().getService().getName())
                                .collect(Collectors.joining(", "));

                String htmlContent = String.format(
                                "<!DOCTYPE html>" +
                                                "<html>" +
                                                "<head>" +
                                                "    <style>" +
                                                "        body { font-family: 'Inter', sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; }"
                                                +
                                                "        .email-container { max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.05); overflow: hidden; }"
                                                +
                                                "        .header { background-color: #3498db; color: #ffffff; padding: 20px; text-align: center; }"
                                                +
                                                "        .content { padding: 20px; color: #333333; }" +
                                                "        .booking-details { border: 1px solid #e0e0e0; border-radius: 4px; padding: 15px; margin-top: 20px; }"
                                                +
                                                "        .detail-item { margin-bottom: 10px; }" +
                                                "        .detail-item span { font-weight: 600; color: #555555; }" +
                                                "        .status-update { text-align: center; margin-top: 20px; }" +
                                                "        .status-badge { display: inline-block; padding: 8px 16px; border-radius: 20px; font-weight: bold; color: white; margin-top: 10px;height:max-content; }"
                                                +
                                                "        .status-badge.booked { background-color: #3498db; }" +
                                                "        .status-badge.in,.progress { background-color: #f39c12; }" +
                                                "        .status-badge.completed { background-color: #2ecc71; }" +
                                                "        .status-badge.cancelled { background-color: #e74c3c; }" +
                                                "        .footer { text-align: center; padding: 20px; font-size: 12px; color: #888888; }"
                                                +
                                                "    </style>" +
                                                "</head>" +
                                                "<body>" +
                                                "    <div class='email-container'>" +
                                                "        <div class='header'>" +
                                                "            <h1>Booking Status Updated</h1>" +
                                                "        </div>" +
                                                "        <div class='content'>" +
                                                "            <p>Hello %s,</p>" +
                                                "            <p>The status for your booking with <strong>%s</strong> has been updated to:</p>"
                                                +
                                                "            <div class='status-update'>" +
                                                "                <span class='status-badge %s'>%s</span>" +
                                                "            </div>" +
                                                "            <p>Here are your booking details for reference:</p>" +
                                                "            <div class='booking-details'>" +
                                                "                <h3>Booking Details:</h3>" +
                                                "                <p class='detail-item'><span>Order ID:</span> %s</p>" +
                                                "                <p class='detail-item'><span>Vehicle:</span> %s %s (%s)</p>"
                                                +
                                                "                <p class='detail-item'><span>Service:</span> %s</p>" +
                                                "                <p class='detail-item'><span>Date:</span> %s</p>" +
                                                "                <p class='detail-item'><span>Time Slot:</span> %s</p>"
                                                +
                                                "                <p class='detail-item'><span>Total Fee:</span> ₹%.2f</p>"
                                                +
                                                "            </div>" +
                                                "            <p>If you have any questions, please contact the garage directly.</p>"
                                                +
                                                "            <p>Best regards,<br>The GearNest Team</p>" +
                                                "        </div>" +
                                                "        <div class='footer'>" +
                                                "            <p>&copy; %d GearNest. All rights reserved.</p>" +
                                                "        </div>" +
                                                "    </div>" +
                                                "</body>" +
                                                "</html>",
                                user.getFirstName(),
                                booking.getGarage().getGarageName(),
                                booking.getStatus().toLowerCase(),
                                booking.getStatus(),
                                booking.getId(),
                                booking.getVehicleMake(),
                                booking.getVehicleModel(),
                                booking.getVehicleNumber(),
                                serviceNames,
                                booking.getBookingDate().toLocalDate(),
                                booking.getTimeSlot(),
                                booking.getTotalFee(),
                                LocalDateTime.now().getYear());

                helper.setText(htmlContent, true);
                mailSender.send(mimeMessage);
        }

        // Method to send OTP email
        public void sendOtpEmail(User user, String otp) throws MessagingException {
                MimeMessage mimeMessage = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

                helper.setFrom("noreply@gearnest.com");
                helper.setTo(user.getEmail());
                helper.setSubject("Your OTP for Booking Cancellation");

                String htmlContent = String.format(
                                "<!DOCTYPE html>" +
                                                "<html>" +
                                                "<head>" +
                                                "    <style>" +
                                                "        body { font-family: 'Inter', sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; }"
                                                +
                                                "        .email-container { max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.05); overflow: hidden; text-align: center; }"
                                                +
                                                "        .header { background-color: #f39c12; color: #ffffff; padding: 20px; text-align: center; }"
                                                +
                                                "        .content { padding: 20px; color: #333333; }" +
                                                "        .otp-code { font-size: 24px; font-weight: bold; letter-spacing: 5px; padding: 10px 20px; border: 2px dashed #f39c12; border-radius: 4px; display: inline-block; margin: 20px 0; }"
                                                +
                                                "        .footer { text-align: center; padding: 20px; font-size: 12px; color: #888888; }"
                                                +
                                                "    </style>" +
                                                "</head>" +
                                                "<body>" +
                                                "    <div class='email-container'>" +
                                                "        <div class='header'>" +
                                                "            <h1>OTP Verification</h1>" +
                                                "        </div>" +
                                                "        <div class='content'>" +
                                                "            <p>Hello %s,</p>" +
                                                "            <p>You have requested to cancel your booking. Please use the following OTP to verify your identity.</p>"
                                                +
                                                "            <div class='otp-code'>%s</div>" +
                                                "            <p>This code is valid for 5 minutes. Do not share it with anyone.</p>"
                                                +
                                                "            <p>Best regards,<br>The GearNest Team</p>" +
                                                "        </div>" +
                                                "        <div class='footer'>" +
                                                "            <p>&copy; %d GearNest. All rights reserved.</p>" +
                                                "        </div>" +
                                                "    </div>" +
                                                "</body>" +
                                                "</html>",
                                user.getFirstName(),
                                otp,
                                LocalDateTime.now().getYear());

                helper.setText(htmlContent, true);
                mailSender.send(mimeMessage);
        }

        public void sendBookingCancellationEmail(User user, Booking booking) throws MessagingException {
                MimeMessage mimeMessage = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

                helper.setFrom("noreply@gearnest.com");
                helper.setTo(user.getEmail());
                helper.setSubject("GearNest Booking Cancellation - Booking #" + booking.getId());

                String serviceNames = booking.getBookingServiceItems().stream()
                                .map(item -> item.getParticularGarageService().getService().getName())
                                .collect(Collectors.joining(", "));

                String htmlContent = String.format(
                                "<!DOCTYPE html>" +
                                                "<html>" +
                                                "<head>" +
                                                "    <style>" +
                                                "        body { font-family: 'Inter', sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; }"
                                                +
                                                "        .email-container { max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.05); overflow: hidden; text-align: center; }"
                                                +
                                                "        .header { background-color: #e74c3c; color: #ffffff; padding: 20px; text-align: center; }"
                                                +
                                                "        .content { padding: 20px; color: #333333; }" +
                                                "        .booking-details { border: 1px solid #e0e0e0; border-radius: 4px; padding: 15px; margin-top: 20px; }"
                                                +
                                                "        .detail-item { margin-bottom: 10px; }" +
                                                "        .detail-item span { font-weight: 600; color: #555555; }" +
                                                "        .footer { text-align: center; padding: 20px; font-size: 12px; color: #888888; }"
                                                +
                                                "    </style>" +
                                                "</head>" +
                                                "<body>" +
                                                "    <div class='email-container'>" +
                                                "        <div class='header'>" +
                                                "            <h1>Booking Cancelled</h1>" +
                                                "        </div>" +
                                                "        <div class='content'>" +
                                                "            <p>Hello %s,</p>" +
                                                "            <p>Your booking with <strong>%s</strong> has been successfully cancelled.</p>"
                                                +
                                                "            <p>Here are the details for your records:</p>" +
                                                "            <div class='booking-details'>" +
                                                "                <h3>Booking Details:</h3>" +
                                                "                <p class='detail-item'><span>Booking ID:</span> %s</p>"
                                                +
                                                "                <p class='detail-item'><span>Vehicle:</span> %s %s (%s)</p>"
                                                +
                                                "                <p class='detail-item'><span>Services:</span> %s</p>" +
                                                "                <p class='detail-item'><span>Date:</span> %s</p>" +
                                                "                <p class='detail-item'><span>Time Slot:</span> %s</p>"
                                                +
                                                "                <p class='detail-item'><span>Total Fee:</span> ₹%.2f</p>"
                                                +
                                                "            </div>" +
                                                "            <p>We hope to see you again soon!</p>" +
                                                "            <p>Best regards,<br>The GearNest Team</p>" +
                                                "        </div>" +
                                                "        <div class='footer'>" +
                                                "            <p>&copy; %d GearNest. All rights reserved.</p>" +
                                                "        </div>" +
                                                "    </div>" +
                                                "</body>" +
                                                "</html>",
                                user.getFirstName(),
                                booking.getGarage().getGarageName(),
                                booking.getId(),
                                booking.getVehicleMake(),
                                booking.getVehicleModel(),
                                booking.getVehicleNumber(),
                                serviceNames,
                                booking.getBookingDate().toLocalDate(),
                                booking.getTimeSlot(),
                                booking.getTotalFee(),
                                LocalDateTime.now().getYear());

                helper.setText(htmlContent, true);
                mailSender.send(mimeMessage);
        }

}
