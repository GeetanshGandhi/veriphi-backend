package com.project.veriphi.ticket;

import com.project.veriphi.payloads.ResoldTicketPayload;
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

    @PostMapping("/resale")
    public ResponseEntity<String> resaleTicket(@RequestBody String ticketNUmber) {
        try{
            String res = ticketService.resaleTicket(ticketNUmber);
            if(res == null) return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
            if(res.equals("no_ticket"))
                return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(res, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error at endpoint resaleTicket: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/getResoldByES")
    public ResponseEntity<List<ResoldTicketPayload>> getResoldByES(@RequestParam long eventId,
                                                                   @RequestParam long venueId,
                                                                   @RequestParam String date,
                                                                   @RequestParam String startTime) {
        try {
            List<ResoldTicketPayload> output = ticketService.getResaleTicketsForES(eventId, venueId, date, startTime);
            if(output == null || output.isEmpty()) return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(output, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error at getResoldByES endpoint: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/buyResoldTickets")
    public ResponseEntity<String> buyResoldTickets(@RequestBody List<String> ticketNumbers) {
        try{
            String res = ticketService.buyResoldTickets(ticketNumbers);
            if(res == null) return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
            if(res.equals("not_all")) return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(res, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error at buyResoldTicket endpoint: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/fetchResoldTicketStatus")
    public ResponseEntity<String> fetchResoldTicketStatus(@RequestBody String ticketNumber) {
        try {
            String out = ticketService.fetchResoldTicketStatus(ticketNumber);
            if(out == null) return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(out, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error while fetchResoldTicketStatus endpoint: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/maketicket")
    public void func(){
        ticketService.initiateTicketingForBookedBookings();
    }

}
