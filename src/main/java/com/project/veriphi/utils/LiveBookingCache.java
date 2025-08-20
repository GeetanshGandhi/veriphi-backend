package com.project.veriphi.utils;

import com.project.veriphi.event_schedule.EventSchedule;
import com.project.veriphi.seat_category.SeatCategory;
import com.project.veriphi.utils.redis.RedisClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class LiveBookingCache {

    private static final long USER_EXPIRATION_TIME = 15 * 60L + 5;

    @Autowired
    private RedisClient redisClient;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

    // key: "EventSchedule":eventId(long):venueId(long):date(dd-MM-yyyy string):startTime(string)
    // field: seatCategoryId (string)
    // value: available number of seats (integer)
    public String initiateForEventSchedule(EventSchedule schedule, List<SeatCategory> availableCategories,
                                           int trialCount){
        if(trialCount == 0) {
            log.error("Maximum initialization trials completed. Cannot try further.");
            return "retry_exhaustion";
        }
        try{
            String key = createEventScheduleKey(schedule);
            Map<String, String> availableSeats = new HashMap<>();
            for(SeatCategory cat : availableCategories) {
                availableSeats.put(cat.getCategoryId(), String.valueOf(cat.getMaxAvailability()));
            }
            String output = redisClient.createHash(key, availableSeats);
            if(output == null){
                log.error("Error occurred in RedisClient.");
                return initiateForEventSchedule(schedule, availableCategories, trialCount - 1);
            } if(output.equals("partial")) {
                return initiateForEventSchedule(schedule, availableCategories, trialCount - 1);
            } else return output;
        } catch (Exception e){
            log.error("Error occurred while initializing cache : {}", e.getMessage());
            return null;
        }
    }

    public String updateSeatCountForEventSchedule(EventSchedule schedule, String categoryId,
                                                  int seatsToDecrease, int retryCount) {
        if(retryCount == 0){
            log.error("Retry count exhausted for updateSeatCountForEventSchedule.");
            return "failure";
        }
        try {
            String key = createEventScheduleKey(schedule);
            String updateResponse = redisClient.incrementHash(key, categoryId, -seatsToDecrease);
            if(updateResponse == null || updateResponse.equals("failure")) {
                log.error("Cannot update seat availability for eventSched and seatCategory. Retrying...");
                return updateSeatCountForEventSchedule(schedule, categoryId, seatsToDecrease, retryCount - 1);
            }
            return updateResponse;
        } catch (Exception e) {
            log.error("Error occurred while updateSeatCountForEventSchedule: {}", e.getMessage());
            return null;
        }
    }

    //cache will expire after 15 minutes automatically
    public String addUserBookingToCache(EventSchedule schedule, String seatCategoryId, String userEmail,
                                        String bookingId, int numberOfSeats) {
        try {
            String key = createUserKey(schedule, seatCategoryId, userEmail, numberOfSeats);
            String output = redisClient.setString(key, bookingId, USER_EXPIRATION_TIME);
            return output.equals("OK") ? "success" : "failure";
        } catch (Exception e) {
            log.error("error while addUserBookingToCache: {}", e.getMessage());
            return null;
        }
    }

    public String isUserPresentInCache(EventSchedule schedule, String seatCategoryId, String userEmail,
                                       int numberOfSeats) {
        try {
            String key = createUserKey(schedule, seatCategoryId, userEmail, numberOfSeats);
            String output = redisClient.getString(key, true);
            if(output == null || output.equals("null")) {
                return null;
            }
            return output;
        } catch (Exception e) {
            log.error("error while isUserPresentInCache: {}", e.getMessage());
            return null;
        }
    }

    public void deleteUserFromCache(EventSchedule schedule, String seatCategoryId, String userEmail,
                                      int numberOfSeats) {
        try{
            String key = createUserKey(schedule, seatCategoryId, userEmail, numberOfSeats);
            redisClient.deleteKey(key);
        } catch (Exception e) {
            log.error("Error while deleting user from cache: {}", e.getMessage());
        }
    }

    private String createUserKey(EventSchedule schedule, String seatCategoryId, String userEmail,
                                 int numberOfSeats) {
        return "User:"+
                userEmail+
                ":"+schedule.getEvent().getEventId()+
                ":"+schedule.getVenue().getVenueId()+
                ":"+dateFormat.format(schedule.getDate())+
                ":"+schedule.getStartTime()+
                ":"+seatCategoryId+
                ":"+numberOfSeats;
    }

    private String createEventScheduleKey(EventSchedule schedule) {
        return "EventSchedule:"+
                schedule.getEvent().getEventId()+
                ":"+schedule.getVenue().getVenueId()+
                ":"+dateFormat.format(schedule.getDate())+
                ":"+schedule.getStartTime();
    }
}
