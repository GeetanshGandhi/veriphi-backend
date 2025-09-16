package com.project.veriphi.seat;

import com.project.veriphi.event.Event;
import com.project.veriphi.event.EventService;
import com.project.veriphi.seat.payloads.AddSeatPayload;
import com.project.veriphi.seat_category.SeatCategory;
import com.project.veriphi.seat_category.SeatCategoryService;
import com.project.veriphi.utils.SeatCategoryIdGenerator;
import com.project.veriphi.utils.SeatIdGenerator;
import com.project.veriphi.venue.Venue;
import com.project.veriphi.venue.VenueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@Slf4j
public class SeatService {

    @Autowired
    SeatRepository seatRepository;
    @Autowired
    EventService eventService;
    @Autowired
    VenueService venueService;
    @Autowired
    SeatCategoryService seatCategoryService;

    public String addSeats(List<AddSeatPayload> seats, long eventId, long venueId) {
        try{
            Event event = eventService.getById(eventId);
            Venue venue = venueService.getById(venueId);
            if(event == null || venue == null) {
                log.error("Cannot proceed addSeat due to unavailability of event/venue. Event: {}, venue: {}",
                        event!=null?event.toString():"null",
                        venue!=null?venue.toString():"null"
                );
                return "even_null";
            }
            HashMap<String, SeatCategory> categories = new HashMap<>();
            List<Seat> seatList = new ArrayList<>();
            List<SeatCategory> categoryList = new ArrayList<>();
            seats.forEach( seat ->{
                String categoryId = SeatCategoryIdGenerator.generateId(eventId, venueId, seat.getCategoryName());
                if(categories.containsKey(categoryId)) {
                    categories.get(categoryId).setMaxAvailability(
                            categories.get(categoryId).getMaxAvailability()+1
                    );
                }
                else {
                    SeatCategory newCat = new SeatCategory(
                            categoryId,
                            seat.getCategoryName(),
                            seat.getCategoryDescription(),
                            seat.getPrice(),
                            1,
                            venue,
                            event
                    );
                    categoryList.add(newCat);
                    categories.put(categoryId, newCat);
                }
                String seatId = SeatIdGenerator.generateId(seat.getSeatNumber(), categoryId);
                Seat newSeat = new Seat(
                        seatId,
                        seat.getSeatNumber(),
                        categoryId,
                        false,
                        categories.get(categoryId)
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

    public List<Seat> getSeatsByCategory(String categoryId) {
        try {
            return seatRepository.findAllByCategoryId(categoryId);
        } catch (Exception e) {
            log.error("Error occurred while getSeatsByCategory: {}", e.getMessage());
            return null;
        }
    }

    public void updateSeatAllotment(List<Pair<Seat, Boolean>> seats) {
        try {
            List<Seat> updated = new ArrayList<>();
            for(Pair<Seat, Boolean> pair: seats) {
                Seat upd = pair.getFirst();
                upd.setAllotment(pair.getSecond());
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
}
