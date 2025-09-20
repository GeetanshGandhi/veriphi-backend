package com.project.veriphi.live_booking;

import com.project.veriphi.booking.Booking;
import com.project.veriphi.booking.BookingService;
import com.project.veriphi.booking.UserBooking;
import com.project.veriphi.event_schedule.EventSchedule;
import com.project.veriphi.seat_category.SeatCategory;
import com.project.veriphi.seat_category.SeatCategoryService;
import com.project.veriphi.user.User;
import com.project.veriphi.user.UserService;
import com.project.veriphi.utils.UserBookingIdGenerator;
import com.project.veriphi.utils.LiveBookingCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class LiveBookingService {

    private static final int TRIAL_COUNT = 5;

    @Autowired
    LiveBookingCache cache;
    @Autowired
    SeatCategoryService scSvc;
    @Autowired
    BookingService bookingService;
    @Autowired
    UserService userService;

    public String initiateBookingProcess(List<EventSchedule> schedules) {
        try {
            int completeCount = 0;
            for(EventSchedule schedule : schedules) {
                log.info("Initiating booking process for schedule {}", schedule);
                List<SeatCategory> categories = scSvc.getByEventAndVenue(schedule.getEvent(), schedule.getVenue());
                String cacheResponse = cache.initiateForEventSchedule(schedule, categories, TRIAL_COUNT);
                if(cacheResponse == null) {
                    log.error("Error occurred in initBookingProcess for eventSchedule: {}", schedule);
                } else if(cacheResponse.equals("retry_exhaustion")) {
                    log.error("Retry exhaustion in initiateForEventSchedule for eventSchedule: {}", schedule);
                } else {
                    log.info("BookingProcess successfully initiated for eventSchedule: {}", schedule);
                    completeCount++;
                }
            }
            return completeCount > 0 ? completeCount == schedules.size() ? "success" : "partial" : "failure";
        } catch (Exception e) {
            log.error("Error while initiating booking process: {}", e.getMessage());
            return null;
        }
    }

    public String initiateBookingProcessForUser(EventSchedule schedule, String categoryId,
                                            String userEmail, int numberOfTickets) {
    try {
        int available = cache.getSeatCountForES(schedule, categoryId);
        if(available<numberOfTickets){
            return "seats_unavailable";
        }
        UserBooking alreadyBooked = bookingService.getByUserAndES(userEmail, schedule);
        String isUserInCache = cache.isUserPresentInCache(schedule, categoryId, userEmail, numberOfTickets);
        if(alreadyBooked!=null || (isUserInCache!=null && !isUserInCache.equals("null"))) {
            return "already_booked";
        }

        String bookingId = UserBookingIdGenerator.createBookingID(userEmail);

        String cacheUserResponse = cache.addUserBookingToCache(
                schedule, categoryId, userEmail, bookingId, numberOfTickets
        );
        if (cacheUserResponse == null || cacheUserResponse.equals("failure")) {
            log.error("could not add user to cache.");
            return null;
        }

        String seatResponse = cache.updateSeatCountForEventSchedule(
                schedule, categoryId, numberOfTickets, 5
        );
        if (seatResponse == null || seatResponse.equals("failure")) {
            log.error("could not update seats in cache.");
            return null;
        }

        return "ok"; // 🔹 return bookingId
    } catch (Exception e) {
        log.error("Error while initiating user booking: {}", e.getMessage());
        return null;
    }
}


    public UserBooking saveUserBooking(String userEmail, EventSchedule schedule, String categoryId, int numberOfSeats){
        String cacheResponse = cache.isUserPresentInCache(schedule, categoryId, userEmail, numberOfSeats);
        if(cacheResponse == null ){
            log.error("User TTL expired for {}.", userEmail);
            return new UserBooking("-2");
        }
        User user = userService.getByEmail(userEmail);
        SeatCategory seatCategory = scSvc.getById(categoryId);

        Booking booking = new Booking(
                new Date(),
                false,
                numberOfSeats,
                "booked",
                schedule,
                seatCategory
        );
        Booking addedBooking = bookingService.createBooking(booking);

        UserBooking userBooking = new UserBooking(
                cacheResponse,
                addedBooking,
                user
        );
        UserBooking addedUserBooking = bookingService.createUserBooking(userBooking);
        cache.deleteUserFromCache(schedule, categoryId, userEmail, numberOfSeats);
        log.info("Booking for user {} successfully saved!", userEmail);
        return addedUserBooking;
    }
}
