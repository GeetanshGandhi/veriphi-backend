package com.project.veriphi.seat_category;

import com.project.veriphi.event.Event;
import com.project.veriphi.event.EventService;
import com.project.veriphi.venue.Venue;
import com.project.veriphi.venue.VenueService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
            return null;
        }
        return scRepo.findAllByEventAndVenue(event, venue);
    }

    public List<SeatCategory> getByEventAndVenue(@NonNull Event event, @NonNull Venue venue) {
        return scRepo.findAllByEventAndVenue(event, venue);
    }

    public SeatCategory getById(String categoryId) {
        try {
            Optional<SeatCategory> sc = scRepo.findById(categoryId);
            if(sc.isEmpty()) {
                log.error("no category found with ID: {}", categoryId);
                return new SeatCategory();
            }
            return sc.get();
        } catch (Exception e) {
            log.error("Error occurred while fetching seatCategory by id: {}", e.getMessage());
            return null;
        }
    }

    public String addSeatCategories(List<SeatCategory> categories) {
        try{
            List<SeatCategory> saved = scRepo.saveAll(categories);
            if(saved.size() == categories.size()){
                log.info("saved {} seat_categories successfully!", categories.size());
                return "success";
            }
            return "failure";
        } catch (Exception e){
            log.error("Error while adding seat categories: {}", e.getMessage());
            return "error";
        }
    }

    public void updateSeatCategory(SeatCategory sc) {
        try{
            SeatCategory saved = scRepo.save(sc);
            if(saved.getCategoryId().equals(sc.getCategoryId())){
                log.info("updated category with id {} successfully!", saved.getCategoryId());
            }
        } catch (Exception e){
            log.error("Error while updating seatCategory with id {}: {}",sc.getCategoryId(), e.getMessage());
        }
    }
}
