package com.project.veriphi.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

    @Autowired
    JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    String senderMail;

    public void confirmBookingMail(String eventName, String venue, String date, String email, String name,
                                   String bookingId) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(senderMail);
            message.setTo(email);
            message.setSubject(AppConstants.MAIL_BOOKING_SUBJECT);
            message.setText(String.format(AppConstants.MAIL_BOOKING_BODY,name,eventName,venue,date,bookingId));

            mailSender.send(message);
            log.info("Booking email successfully sent to {}", email);
        } catch (Exception e) {
            log.error("Error occurred while sending booking email to {} : {}", email, e.getMessage());
        }
    }

    public void random(String eventName, String venue, String date, String email, String name,
                                   String bookingId) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(senderMail);
            message.setTo(email);
            message.setSubject(AppConstants.MAIL_BOOKING_SUBJECT);
            message.setText(String.format(AppConstants.MAIL_BOOKING_BODY,name,eventName,venue,date,bookingId));

            mailSender.send(message);
            log.info("Booking email successfully sent to {}", email);
        } catch (Exception e) {
            log.error("Error occurred while sending booking email to {} : {}", email, e.getMessage());
        }
    }
}
