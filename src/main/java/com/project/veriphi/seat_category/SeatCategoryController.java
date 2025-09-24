package com.project.veriphi.seat_category;

import com.project.veriphi.event_schedule.EventSchedule;
import com.project.veriphi.event_schedule.EventScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/seatCategory")
public class SeatCategoryController {

    @Autowired
    SeatCategoryService scService;
    @Autowired
    EventScheduleService esSvc;

    @GetMapping("/getByEventAndVenue")
    public ResponseEntity<List<SeatCategory>> getByEventAndVenue(@RequestParam("eventId") long eventId,
                                             @RequestParam("venueId") long venueId,
                                                                 @RequestParam String date,
                                                                 @RequestParam String startTime){
        EventSchedule es = esSvc.getById(eventId, venueId, date, startTime);
        if(es == null) {
            return new ResponseEntity<>(null, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        List<SeatCategory> found = scService.getByEventSchedule(es);
        if(found == null){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(found, HttpStatus.OK);
    }
}
