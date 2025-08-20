package com.project.veriphi.live_booking;

import com.project.veriphi.booking.Booking;
import com.project.veriphi.booking.BookingService;
import com.project.veriphi.event_schedule.EventSchedule;
import com.project.veriphi.event_schedule.EventScheduleService;
import com.project.veriphi.seat_category.SeatCategory;
import com.project.veriphi.seat_category.SeatCategoryService;
import com.project.veriphi.user.User;
import com.project.veriphi.user.UserService;
import com.project.veriphi.utils.BookingIdGenerator;
import com.project.veriphi.utils.LiveBookingCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
@Slf4j
public class LiveBookingService {

    private static final int TRIAL_COUNT = 5;
    private static final int MAXIMUM_USERS_PROCESSING = 1000;

    private final HashMap<String, Integer> currentUsersProcessing = new HashMap<>();

    @Autowired
    LiveBookingCache cache;
    @Autowired
    SeatCategoryService scSvc;
    @Autowired
    BookingService bookingService;
    @Autowired
    UserService userService;
    @Autowired
    EventScheduleService esSvc;

    public String initiateBookingProcess(List<EventSchedule> schedules) {
        try {
            int completeCount = 0;
            for(EventSchedule schedule : schedules) {
                List<SeatCategory> categories = scSvc.getByEventAndVenue(schedule.getEvent(), schedule.getVenue());
                String cacheResponse = cache.initiateForEventSchedule(schedule, categories, TRIAL_COUNT);
                if(cacheResponse == null) {
                    log.error("Error occurred in initBookingProcess for eventSchedule: {}", schedule.toString());
                } else if(cacheResponse.equals("retry_exhaustion")) {
                    log.error("Retry exhaustion in initiateForEventSchedule for eventSchedule: {}",
                            schedule.toString());
                } else {
                    log.info("BookingProcess successfully initiated for eventSchedule: {}", schedule.toString());
                    completeCount++;
                }
            }
            return completeCount > 0 ? completeCount == schedules.size() ? "success" : "partial" : "failure";
        } catch (Exception e) {
            log.error("Error while initiating booking process: {}", e.getMessage());
            return null;
        }
    }

    public String initiateBookingProcessForUser(EventSchedule schedule, String categoryId, String userEmail,
                                                int numberOfTickets) {
        try {
            String bookingId = BookingIdGenerator.createBookingID(userEmail);
            String cacheResponse = cache.addUserBookingToCache(schedule, categoryId, userEmail, bookingId,
                    numberOfTickets);
            if(cacheResponse == null || cacheResponse.equals("failure")) {
                log.error("could not add user to cache.");
                return null;
            }
            return cacheResponse;
        } catch (Exception e) {
            log.error("Error while initiating user booking: {}", e.getMessage());
            return null;
        }
    }

    public Booking saveUserBooking(String userEmail, EventSchedule schedule, String categoryId, int numberOfSeats){
        String cacheResponse = cache.isUserPresentInCache(schedule, categoryId, userEmail, numberOfSeats);
        if(cacheResponse == null ){
            log.error("User TTL expired for {}.", userEmail);
            return new Booking("ttlexpired");
        }
        User user = userService.getByEmail(userEmail);
        SeatCategory seatCategory = scSvc.getById(categoryId);

        Booking booking = new Booking(
                cacheResponse,
                new Date(),
                false,
                numberOfSeats,
                "booked",
                user,
                schedule,
                seatCategory
        );

        Booking addedBooking = bookingService.createBooking(booking);
        cache.deleteUserFromCache(schedule, categoryId, userEmail, numberOfSeats);
        log.info("Booking for user {} successfully saved!", userEmail);
        return addedBooking;
    }
}
