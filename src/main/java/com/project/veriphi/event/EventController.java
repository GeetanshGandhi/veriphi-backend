package com.project.veriphi.event;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/event")
public class EventController {

    @Autowired
    EventService eventService;

    @PostMapping("/add")
    public ResponseEntity<Event> addEvent(@RequestBody Event event){
        Event response = eventService.addEvent(event);
        if(response==null){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<Event> updateEvent(@RequestBody Event event){
        Event response = eventService.updateEvent(event);
        if(response==null){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteEvent(@RequestParam("eventId") long eventId){
        boolean response = eventService.deleteEvent(eventId);
        if(!response){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @PutMapping("/updateApproval")
    public ResponseEntity<String> updateApproval(@RequestParam("eventId") long eventId,
                                                 @RequestParam("status") boolean status) {
        String res = eventService.updateApproval(eventId, status);
        if(res == null) {
            return new ResponseEntity<>("error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if(res.equals("no id found")) {
            return new ResponseEntity<>(res, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/list")
    public ResponseEntity<List<Event>> getAllEvents(@RequestParam(required = false) Boolean approved){
        List<Event> events = eventService.getAllEvents(approved);
        if(events == null){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(events, HttpStatus.OK);
    }
}
