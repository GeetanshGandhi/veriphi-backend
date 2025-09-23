package com.project.veriphi.ticket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/ticket")
public class TicketController {

    @Autowired
    TicketService ticketService;

    @GetMapping("/getAllById")
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

    @GetMapping("/getAllByBooking")
    public ResponseEntity<List<Ticket>> getAllByBooking(@RequestParam("bookingId") String bookingId) {
        try {
            List<Ticket> found = ticketService.getAllByBooking(bookingId);
            if(found == null ){
                return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            if(found.isEmpty()) {
                return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(found, HttpStatus.OK);
        } catch (Exception e) {
            log.error("error occurred while getAllByBooking endpoint: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
