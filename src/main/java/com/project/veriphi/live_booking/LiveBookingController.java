package com.project.veriphi.live_booking;

import com.project.veriphi.booking.UserBooking;
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
    public ResponseEntity<String> initiateBookingForUser(@RequestParam("eventId") long eventId,
                                                @RequestParam("venueId") long venueId,
                                                @RequestParam("date") String date,
                                                @RequestParam("startTime") String startTime,
                                                @RequestParam("categoryId") String categoryId,
                                                @RequestParam("userEmail") String email,
                                                @RequestParam("numberOfSeats") int numberSeats) {
        try {
            EventSchedule schedule = esSvc.getById(eventId, venueId, date, startTime);
            String response = liveBookingService.initiateBookingProcessForUser(
                    schedule, categoryId, email, numberSeats
            );
            return switch (response) {
                case "seats_unavailable" -> new ResponseEntity<>(
                        response,
                        HttpStatus.CONFLICT
                );
                case "already_booked" -> new ResponseEntity<>(
                        response,
                        HttpStatus.ALREADY_REPORTED
                );
                case "ok" -> new ResponseEntity<>(
                        response,
                        HttpStatus.OK
                );
                default -> new ResponseEntity<>(
                        "error",
                        HttpStatus.INTERNAL_SERVER_ERROR
                );
            };
        } catch (Exception e) {
            log.error("Error at initiateBookingForUser endpoint: {}", e.getMessage());
            return new ResponseEntity<>(
                "error",
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }


    @PostMapping("/confirmBooking")
    public ResponseEntity<UserBooking> confirmBooking(@RequestParam("eventId") long eventId,
                                                  @RequestParam("venueId") long venueId,
                                                  @RequestParam("date") String date,
                                                  @RequestParam("startTime") String startTime,
                                                  @RequestParam("categoryId") String categoryId,
                                                  @RequestParam("userEmail") String email,
                                                  @RequestParam("numberOfSeats") int numberSeats){
        try{
            EventSchedule schedule = esSvc.getById(eventId, venueId, date, startTime);
            UserBooking response = liveBookingService.saveUserBooking(email, schedule, categoryId, numberSeats);

            if(response == null)
                return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);

            if(response.getBookingId().equalsIgnoreCase("-2")) {
                return new ResponseEntity<>(null, HttpStatus.REQUEST_TIMEOUT);
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error at confirmBooking endpoint: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
