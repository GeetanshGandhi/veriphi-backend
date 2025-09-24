package com.project.veriphi.seat_category;

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
}
