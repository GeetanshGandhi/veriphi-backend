package com.project.veriphi.utils;

import com.project.veriphi.event_schedule.EventSchedule;
import com.project.veriphi.seat_category.SeatCategory;
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

    @Autowired
    private RedisClient redisClient;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

    // key: level 1: <eventId>+"-"+<venueId>, level 2: <date> + "-" <time>, both as string
    // level 3: seat categoryId
    // value: available number of seats (integer)
    public String initiateForEventSchedule(EventSchedule schedule, List<SeatCategory> availableCategories,
                                           int trialCount){
        if(trialCount == 0) {
            log.error("Maximum initialization trials completed. Cannot try further.");
            return null;
        }
        try{
            String key = "EventSchedule:"+dateFormat.format(schedule.getDate())+":"+schedule.getStartTime();
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
            return "error";
        }
    }

    //cache will expire after 15 minutes automatically
//    public String addUserBookingToCache(String userEmail, String bookingId, int numberOfSeats) {
//        try {
//            String output = redisClient
//        }
//    }
}
