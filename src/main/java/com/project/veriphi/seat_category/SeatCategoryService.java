package com.project.veriphi.seat_category;

import com.project.veriphi.event.Event;
import com.project.veriphi.event.EventService;
import com.project.veriphi.venue.Venue;
import com.project.veriphi.venue.VenueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class SeatCategoryService {

    @Autowired
    SeatCategoryRepository scRepo;
    @Autowired
    EventService eventService;
    @Autowired
    VenueService venueService;

    public List<SeatCategory> getByEventAndVenue(long eventId, long venueId){
        Event event = eventService.getById(eventId);
        Venue venue = venueService.getById(venueId);
        if(event == null || venue == null){
            log.warn("No matching seat category found. Venue: {}, Event: {}",
                    venue!=null?venue.toString():"null",
                    event!=null?event.toString():"null");
        }
        return scRepo.findAllByEventAndVenue(event, venue);
    }
}
