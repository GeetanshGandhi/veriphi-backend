package com.project.veriphi.ticket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/ticket")
public class TicketController {

    @Autowired
    TicketService ticketService;

    @PostMapping("/initForEventSched")
    public ResponseEntity<String> init(@RequestParam("eventId") long eventId,
                                       @RequestParam("venueId") long venueId,
                                       @RequestParam("startTime") String startTime,
                 @RequestParam("date") @DateTimeFormat(pattern = "dd-MM-yyyy") Date date) {
        try {
            ticketService.initiateTicketingForEventSchedule(eventId, venueId, date, startTime);
            return new ResponseEntity<>("processing", HttpStatus.OK);
        } catch (Exception e){
            e.printStackTrace();
            log.error("Error occurred while ticket generation: {}", e.getMessage());
            return new ResponseEntity<>("failure", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
