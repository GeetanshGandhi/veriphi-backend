package com.project.veriphi.event_schedule;

import com.project.veriphi.event.Event;
import com.project.veriphi.event.EventService;
import com.project.veriphi.live_booking.LiveBookingService;
import com.project.veriphi.venue.Venue;
import com.project.veriphi.venue.VenueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class EventScheduleService {

    @Autowired
    EventScheduleRepository esRepo;
    @Autowired
    VenueService venueService;
    @Autowired
    EventService eventService;
    @Autowired
    LiveBookingService liveBookingService;

    @Scheduled(cron = "0 0 12 * * *", zone = "IST")
    private void scheduledSaleStart(){
        log.info("Initiating ticket sale for today.");
        String currentDate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        List<EventSchedule> eligibleSchedules = esRepo.findAllBySaleLiveAndScheduledSaleStart(false, currentDate);
        log.info("Number of eligible schedules found that can go live for sale today: {}", eligibleSchedules.size());
        if(eligibleSchedules.isEmpty()) return;
        for(EventSchedule es : eligibleSchedules){
            es.setSaleLive(true);
        }
        esRepo.saveAll(eligibleSchedules);
        String initCacheResponse = liveBookingService.initiateBookingProcess(eligibleSchedules);
        for(int i = 0; i<5; i++){
            if(initCacheResponse.equals("success")) break;
            initCacheResponse = liveBookingService.initiateBookingProcess(eligibleSchedules);
            if(i == 4 && !initCacheResponse.equals("success")) {
                log.error("Failed to initialize cache for one or more EventSchedules.");
            }
        }
    }

    public List<EventSchedule> getEventScheduleByEvent(long eventId) {
        Event event = eventService.getById(eventId);
        if(event == null){
            log.error("Cannot find event with ID {}. Returning empty list of eventSchedule", eventId);
            return new ArrayList<>();
        }
        return esRepo.findAllByEvent(event);
    }

    public List<EventSchedule> getByEventAndVenue(long eventId, long venueId){
        Event event = eventService.getById(eventId);
        Venue venue = venueService.getById(venueId);
        if(event == null || venue == null){
            log.error("Cannot find schedule for given eventId {} and venueId {}. Object present: {}. Returning empty " +
                      "list of eventSchedule",
                    eventId, venueId, event==null?venue:event);
            return new ArrayList<>();
        }
        return esRepo.findAllByEventAndVenue(event, venue);
    }

    public List<EventSchedule> getByEventAndCity(long eventId, String pinCode){
        List<Venue> venues = venueService.getVenuesByCity(pinCode);
        Event event = eventService.getById(eventId);
        if(venues.isEmpty() || event == null){
            log.warn("Invalid event or city details. Event: {}, Venue list: {}", event, venues);
            return new ArrayList<>();
        }
        List<EventSchedule> eventSchedules = new ArrayList<>();
        for(Venue venue: venues){
            eventSchedules.addAll(esRepo.findAllByVenue(venue));
        }
        return eventSchedules;
    }

    public EventSchedule getById(long eventId, long venueId, String date, String startTime){
        Event event = eventService.getById(eventId);
        Venue venue = venueService.getById(venueId);
        System.out.println("event: "+event);
        System.out.println("venue: "+venue);
        if(event == null || venue == null){
            log.error("Cannot find schedule for given eventId {} and venueId {}. Object present: {}. Returning null",
                    eventId, venueId, event==null?venue:event);
            return null;
        }
        return esRepo.findByEventAndVenueAndDateAndStartTime(event, venue, date, startTime);
    }

    public EventSchedule addEventSchedule(EventSchedule eventSchedule){
        try{
            EventSchedule saved = esRepo.save(eventSchedule);
            log.info("Event Schedule saved successfully.");
            return saved;
        } catch (Exception e){
            log.error("Error occurred during saving the event schedule. Error: {}", e.getMessage());
            return null;
        }
    }

    public List<EventSchedule> getByCity(String pinCode) {
        return esRepo.findByVenue_City_PinCode(pinCode);
    }

}
