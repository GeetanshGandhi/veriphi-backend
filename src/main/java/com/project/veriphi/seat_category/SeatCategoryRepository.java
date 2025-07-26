package com.project.veriphi.seat_category;

import com.project.veriphi.event.Event;
import com.project.veriphi.venue.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatCategoryRepository extends JpaRepository<SeatCategory, String> {

    List<SeatCategory> findAllByEventAndVenue(Event event, Venue venue);
}
