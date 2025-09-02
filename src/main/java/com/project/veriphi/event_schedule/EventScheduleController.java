package com.project.veriphi.event_schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/eventSchedule")
@Slf4j
public class EventScheduleController {
    @Autowired
    EventScheduleService esService;

    @PostMapping("/add")
    public ResponseEntity<EventSchedule> addSchedule(@RequestBody EventSchedule es){
        EventSchedule saved = esService.addEventSchedule(es);
        if(saved == null){
            log.warn("null event schedule retrieved.");
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(saved, HttpStatus.OK);
    }

    @PostMapping("/getByEvent")
    public ResponseEntity<List<EventSchedule>> getByEvent(@RequestParam("eventId") long eventId){
        try{
            return new ResponseEntity<>(esService.getEventScheduleByEvent(eventId), HttpStatus.OK);
        } catch (Exception e){
            log.error("Error occurred while executing getByEvent. Error: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/getByEventAndCity")
    public ResponseEntity<List<EventSchedule>> getByCityAndEvent(@RequestParam("eventId") long eventId,
                                                                 @RequestParam("pinCode") String pinCode){
        try{
            return new ResponseEntity<>(esService.getByEventAndCity(eventId, pinCode), HttpStatus.OK);
        } catch (Exception e){
            log.error("Error occurred while executing getByCityAndEvent. Error: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/getByEventAndVenue")
    public ResponseEntity<List<EventSchedule>> getByEventAndVenue(@RequestParam("eventId") long eventId,
                                                                  @RequestParam("venueId") long venueId){
        try{
            return new ResponseEntity<>(esService.getByEventAndVenue(eventId, venueId), HttpStatus.OK);
        } catch (Exception e){
            log.error("Error occurred while executing getByEventAndVenue. Error: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/getById")
    public ResponseEntity<EventSchedule> getById(@RequestParam("eventId") long eventId,
                                                 @RequestParam("venueId") long venueId,
                                                 @RequestParam("date") String date,
                                                 @RequestParam("startTime") String startTime){
        try{
            System.out.println(date);
            return new ResponseEntity<>(esService.getById(eventId, venueId, date, startTime), HttpStatus.OK);
        } catch (Exception e){
            log.error("Error occurred while executing getById. Error: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
