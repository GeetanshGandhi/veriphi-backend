package com.project.veriphi.seat_category;

import com.project.veriphi.event_schedule.EventSchedule;
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

    public List<SeatCategory> getByEventSchedule(EventSchedule es) {
        return scRepo.findAllByEventIdAndVenueIdAndDateAndStartTime(
                es.getEvent().getEventId(),
                es.getVenue().getVenueId(),
                es.getDate(),
                es.getStartTime()
        );
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
