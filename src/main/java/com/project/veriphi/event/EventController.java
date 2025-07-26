package com.project.veriphi.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/event")
public class EventController {

    @Autowired
    private EventService eventService;

    @PostMapping("/add")
    public ResponseEntity<Event> addEvent(@RequestBody Event event){
        Event response = eventService.addEvent(event);
        if(response==null){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/update")
    public ResponseEntity<Event> updateEvent(@RequestBody Event event){
        Event response = eventService.updateEvent(event);
        if(response==null){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/delete")
    public ResponseEntity<String> deleteEvent(@RequestParam("eventId") long eventId){
        boolean response = eventService.deleteEvent(eventId);
        if(!response){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(null, HttpStatus.OK);
    }
}
