package com.project.veriphi.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/booking")
public class BookingController {

    @Autowired
    BookingService bookingService;

    @PostMapping("/add")
    public ResponseEntity<Booking> addBooking(@RequestBody Booking booking) {
        Booking saved = bookingService.createBooking(booking);
        return new ResponseEntity<>(saved,
                saved == null ? HttpStatus.INTERNAL_SERVER_ERROR : HttpStatus.OK
        );
    }
}
