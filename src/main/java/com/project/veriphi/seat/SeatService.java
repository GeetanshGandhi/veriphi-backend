package com.project.veriphi.seat;

import com.project.veriphi.event_schedule.EventSchedule;
import com.project.veriphi.event_schedule.EventScheduleService;
import com.project.veriphi.payloads.AddSeatPayload;
import com.project.veriphi.seat_category.SeatCategory;
import com.project.veriphi.seat_category.SeatCategoryService;
import com.project.veriphi.utils.SeatCategoryIdGenerator;
import com.project.veriphi.utils.SeatIdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class SeatService {

    @Autowired
    SeatRepository seatRepository;
    @Autowired
    SeatCategoryService seatCategoryService;
    @Autowired
    EventScheduleService esSvc;

    public String addSeats(List<AddSeatPayload> seats, long eventId, long venueId, String date, String startTime) {
        try{
            EventSchedule es = esSvc.getById(eventId, venueId, date, startTime);
            if(es == null) {
                log.error("Cannot proceed addSeat due to unavailability of eventSchedule.");
                return "even_null";
            }
            HashMap<String, SeatCategory> categories = new HashMap<>();
            List<Seat> seatList = new ArrayList<>();
            List<SeatCategory> categoryList = new ArrayList<>();
            seats.forEach( seat ->{
                String categoryId = SeatCategoryIdGenerator.generateId(eventId, venueId, date,
                        startTime, seat.getCategoryName());
                if(categories.containsKey(categoryId)) {
                    categories.get(categoryId).setMaxAvailability(
                            categories.get(categoryId).getMaxAvailability()+1
                    );
                    categories.get(categoryId).setCurrentAvailability(
                            categories.get(categoryId).getCurrentAvailability()+1
                    );
                }
                else {
                    SeatCategory newCat = new SeatCategory(
                            categoryId,
                            seat.getCategoryName(),
                            seat.getCategoryDescription(),
                            seat.getPrice(),
                            1,
                            1,
                            es.getEvent().getEventId(),
                            es.getVenue().getVenueId(),
                            es.getDate(),
                            es.getStartTime()
                    );
                    categoryList.add(newCat);
                    categories.put(categoryId, newCat);
                }
                String seatId = SeatIdGenerator.generateId(seat.getSeatNumber(), categoryId);
                Seat newSeat = new Seat(
                        seatId,
                        seat.getSeatNumber(),
                        categoryId,
                        false
                );
                seatList.add(newSeat);
            });
            List<Seat> saved = seatRepository.saveAll(seatList);
            String catResponse = seatCategoryService.addSeatCategories(categoryList);
            if(saved.size() == seats.size() && catResponse.equals("success")) {
                log.info("Successfully added {} seats and {} categories.", saved.size(), categoryList.size());
                return "success";
            }
            log.error("seat saving: {}, category saving: {}",
                    saved.size()==seats.size()?"success":"failure",
                    catResponse);
            return "failure";
        } catch (Exception e){
            log.error("Error while adding seats: {}", e.getMessage());
            return "error";
        }
    }

    public void updateSeatAllotment(List<Pair<Seat, Boolean>> seats) {
        try {
            List<Seat> updated = new ArrayList<>();
            for(Pair<Seat, Boolean> pair: seats) {
                Seat upd = pair.getFirst();
                upd.setAllotment(pair.getSecond());
                updated.add(upd);
            }
            seatRepository.saveAll(updated);
            log.info("Updated {} seat allotments successfully", seats.size());
        } catch (Exception e){
            log.error("Error occurred while updateSeatAllotment: {}", e.getMessage());
        }
    }

    public List<Seat> getByCategoryAndAllotmentAndLimit(String categoryId, boolean allotment, int limit) {
        try {
            Pageable pageable = PageRequest.of(0, limit);
            return seatRepository.findAllByCategoryIdAndAllotment(categoryId, allotment, pageable);
        } catch (Exception e) {
            log.error("Error while getByCategoryAndAllotmentAndLimit: {}", e.getMessage());
            return null;
        }
    }

    public Seat getById(String id) {
        try{
            Optional<Seat> found = seatRepository.findById(id);
            return found.orElse(null);
        } catch (Exception e) {
            log.error("Error while getById: {}", e.getMessage());
            return null;
        }
    }
}
