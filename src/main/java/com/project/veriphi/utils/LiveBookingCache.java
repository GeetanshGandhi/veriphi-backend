package com.project.veriphi.utils;

import com.project.veriphi.event_schedule.EventSchedule;
import com.project.veriphi.seat_category.SeatCategory;
import com.project.veriphi.utils.redis.RedisClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class LiveBookingCache {

    @Autowired
    RedisClient redisClient;

    // key: "EventSchedule":eventId(long):venueId(long):date(dd-MM-yyyy string):startTime(string)
    // field: seatCategoryId (string)
    // value: available number of seats (integer)
    public String initiateForEventSchedule(EventSchedule schedule, List<SeatCategory> availableCategories,
                                           int trialCount){
        log.info("Initiating cache for schedule {}", createEventScheduleKey(schedule));
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
            }
            return output;
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
                log.error("Cannot update seat availability for eventSchedule and seatCategory. Retrying...");
                return updateSeatCountForEventSchedule(schedule, categoryId, seatsToDecrease, retryCount - 1);
            }
            return updateResponse;
        } catch (Exception e) {
            log.error("Error occurred while updateSeatCountForEventSchedule: {}", e.getMessage());
            return null;
        }
    }

    public int getSeatCountForES(EventSchedule es, String categoryId) {
        try{
            String key = createEventScheduleKey(es);
            String cacheOut = redisClient.getHashField(key, categoryId);
            return Integer.parseInt(cacheOut);
        } catch (Exception e) {
            log.error("Error occurred while getSeatCountForES: {}", e.getMessage());
            return -1;
        }
    }

    public Boolean checkAvailabilityForES(EventSchedule es) {
        try{
            String key = createEventScheduleKey(es);
            Map<String, String> out = redisClient.getHash(key);
            if(out == null) return null;
            for(String s: out.values()) {
                if(Integer.parseInt(s)>0) return false;
            }
            return true;
        } catch (Exception e) {
            log.error("Error while checkAvailabilityForES: {}", e.getMessage());
            return null;
        }
    }

    //cache will expire after 15 minutes automatically
    public String addUserBookingToCache(EventSchedule schedule, String seatCategoryId, String userEmail,
                                        String bookingId, int numberOfSeats) {
        try {
            String key = createUserKey(schedule, seatCategoryId, userEmail, numberOfSeats);
            String output = redisClient.setString(key, bookingId, AppConstants.USER_EXPIRATION_TIME);
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
        return "User#"+
                userEmail+
                "#"+schedule.getEvent().getEventId()+
                "#"+schedule.getVenue().getVenueId()+
                "#"+schedule.getDate()+
                "#"+schedule.getStartTime()+
                "#"+seatCategoryId+
                "#"+numberOfSeats;
    }

    private String createEventScheduleKey(EventSchedule schedule) {
        return "EventSchedule#"+
                schedule.getEvent().getEventId()+
                "#"+schedule.getVenue().getVenueId()+
                "#"+schedule.getDate()+
                "#"+schedule.getStartTime();
    }
}
