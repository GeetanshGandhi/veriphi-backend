package com.project.veriphi.event_schedule;

import com.project.veriphi.event.Event;
import com.project.veriphi.venue.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface EventScheduleRepository extends JpaRepository<EventSchedule, EventScheduleId> {

    List<EventSchedule> findAllByVenue(Venue venue);
    List<EventSchedule> findAllByEvent(Event event);
    List<EventSchedule> findAllByEventAndVenue(Event event, Venue venue);
    List<EventSchedule> findAllBySaleLiveAndScheduledSaleStart(boolean saleLive, Date scheduledSaleStart);
    EventSchedule findByEventAndVenueAndDateAndStartTime(Event event, Venue venue, Date date, String startTime);
}
