package com.project.veriphi.utils;

import com.project.veriphi.event_schedule.EventSchedule;
import com.project.veriphi.seat_category.SeatCategory;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class LiveBookingCache {

    // key: level 1: <eventId>+"-"+<venueId>, level 2: <date> + "-" <time>, both as string
    // level 3: seat categoryId
    // value: available number of seats (integer)
    private static Map<String, Map<String, Map<String, Integer>>> map = new HashMap<>();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
//    public String initiateForEventSchedule(EventSchedule schedule, List<SeatCategory> availableCategories){
//
//    }
}
