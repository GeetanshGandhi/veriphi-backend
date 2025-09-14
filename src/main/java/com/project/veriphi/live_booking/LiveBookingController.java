package com.project.veriphi.live_booking;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.veriphi.booking.Booking;
import com.project.veriphi.event_schedule.EventSchedule;
import com.project.veriphi.event_schedule.EventScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/liveBooking")
@Slf4j
public class LiveBookingController {

    @Autowired
    LiveBookingService liveBookingService;
    @Autowired
    EventScheduleService esSvc;

@PostMapping("/initUser")
public ResponseEntity<?> initiateBookingForUser(@RequestParam("eventId") long eventId,
                                                @RequestParam("venueId") long venueId,
                                                @RequestParam("date") String date,
                                                @RequestParam("startTime") String startTime,
                                                @RequestParam("categoryId") String categoryId,
                                                @RequestParam("userEmail") String email,
                                                @RequestParam("numberOfSeats") int numberSeats) {
    try {
        EventSchedule schedule = esSvc.getById(eventId, venueId, date, startTime);
        String bookingId = liveBookingService.initiateBookingProcessForUser(
                schedule, categoryId, email, numberSeats
        );

        if (bookingId != null) {
            return new ResponseEntity<>(
                String.format("{\"status\":\"OK\",\"bookingId\":\"%s\"}", bookingId),
                HttpStatus.OK
            );
        }
        return new ResponseEntity<>(
            "{\"status\":\"Error\",\"message\":\"Failed to init booking\"}",
            HttpStatus.INTERNAL_SERVER_ERROR
        );
    } catch (Exception e) {
        log.error("Error at initiateBookingForUser endpoint: {}", e.getMessage());
        return new ResponseEntity<>(
            "{\"status\":\"Error\",\"message\":\"Exception occurred\"}",
            HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}


    @PostMapping("/confirmBooking")
    public ResponseEntity<Booking> confirmBooking(@RequestParam("eventId") long eventId,
                                                  @RequestParam("venueId") long venueId,
                                                  @RequestParam("date") String date,
                                                  @RequestParam("startTime") String startTime,
                                                  @RequestParam("categoryId") String categoryId,
                                                  @RequestParam("userEmail") String email,
                                                  @RequestParam("numberOfSeats") int numberSeats){
        try{
            EventSchedule schedule = esSvc.getById(eventId, venueId, date, startTime);
            Booking response = liveBookingService.saveUserBooking(email, schedule, categoryId, numberSeats);

            if(response == null)
                return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);

            if(response.getBookingId().equalsIgnoreCase("TTL_expired")) {
                return new ResponseEntity<>(null, HttpStatus.REQUEST_TIMEOUT);
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error at confirmBooking endpoint: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
