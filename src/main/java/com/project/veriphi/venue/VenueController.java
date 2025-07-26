package com.project.veriphi.venue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/venue")
public class VenueController {

    @Autowired
    public VenueService venueService;

    @PostMapping("/add")
    public ResponseEntity<Venue> addVenue(@RequestBody Venue venue){
        Venue saved = venueService.addVenue(venue);
        if(saved == null){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(saved, HttpStatus.OK);
    }

    @PostMapping("/delete")
    public ResponseEntity<String> deleteVenue(@RequestBody int id){
        boolean isDeleted = venueService.deleteVenue(id);
        if(!isDeleted){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @PostMapping("/getByCity")
    public List<Venue> getByCity(@RequestBody String pinCode){
        return venueService.getVenuesByCity(pinCode);
    }
}
