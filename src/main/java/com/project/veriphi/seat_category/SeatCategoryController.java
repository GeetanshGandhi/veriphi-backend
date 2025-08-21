package com.project.veriphi.seat_category;

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

    @PostMapping("/getByEventAndVenue")
    public ResponseEntity<List<SeatCategory>> getByEventAndVenue(@RequestParam("eventId") long eventId,
                                             @RequestParam("venueId") long venueId){
        List<SeatCategory> found = scService.getByEventAndVenue(eventId, venueId);
        if(found == null){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(found, HttpStatus.OK);
    }
}
