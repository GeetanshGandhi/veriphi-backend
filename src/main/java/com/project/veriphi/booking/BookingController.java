package com.project.veriphi.booking;

import com.project.veriphi.payloads.UserBookingDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/booking")
@Slf4j
public class BookingController {

    @Autowired
    BookingService bookingService;

    @GetMapping("/getForUser")
    public ResponseEntity<List<UserBookingDetails>> getBookingsForUser(@RequestBody String email) {
        try {
            List<UserBookingDetails> bookings = bookingService.getBookingsForUser(email);
            if(bookings == null) {
                return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            if(!bookings.isEmpty() && bookings.getFirst().getUserBookingId().equalsIgnoreCase("-1")) {
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNPROCESSABLE_ENTITY);
            }
            return new ResponseEntity<>(bookings, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error occurred while retrieving bookings for user: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
