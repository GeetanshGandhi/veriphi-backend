package com.project.veriphi.seat_category;

import com.project.veriphi.seat.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatCategoryRepository extends JpaRepository<SeatCategory, String> {

    List<SeatCategory> findAllByEventIdAndVenueIdAndDateAndStartTime(long eventId,
                                                                     long venueId,
                                                                     String date,
                                                                     String startTime
    );
    SeatCategory findByEventIdAndVenueIdAndDateAndStartTimeAndName(long eventId,
                                                           long venueId,
                                                           String date,
                                                           String startTime,
                                                           String name
    );
}
