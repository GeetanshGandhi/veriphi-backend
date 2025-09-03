package com.project.veriphi.ticket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

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
                                       @RequestParam("date") String date) {
        try {
            ticketService.initiateTicketingForEventSchedule(eventId, venueId, date, startTime);
            return new ResponseEntity<>("processing", HttpStatus.PROCESSING);
        } catch (Exception e){
            log.error("Error occurred while ticket generation: {}", e.getMessage());
            return new ResponseEntity<>("failure", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/getAllById")
    public ResponseEntity<List<Ticket>> getAllById(@RequestBody List<String> ticketNumbers) {
        try {
            List<Ticket> found = ticketService.getAllById(ticketNumbers);
            if(found == null ){
                return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            if(found.isEmpty()) {
                return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(found, HttpStatus.OK);
        } catch (Exception e) {
            log.error("error occurred while getAllById endpoint: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
