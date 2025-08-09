package com.project.veriphi.utils;

public class SeatCategoryIdGenerator {

    public static String generateId(long eventId, long venueId, String name) {
        return eventId+"-"+venueId+"-"+name;
    }
}
