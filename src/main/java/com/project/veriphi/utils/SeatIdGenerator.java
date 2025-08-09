package com.project.veriphi.utils;

public class SeatIdGenerator {

    public static String generateId(String seatNumber, String categoryId){
        return categoryId+"-"+seatNumber;
    }
}
