package com.project.veriphi.utils;

public class BookingIdGenerator {

    public static String createBookingID(String userEmail){
        String user = userEmail.split("@")[0];
        StringBuilder generatedId = new StringBuilder();
        for(int i = 1; i<user.length(); i+=2) generatedId.append(user.charAt(i));
        for(int i = 0; i<user.length(); i+=2) generatedId.append(user.charAt(i));
        generatedId.append(System.currentTimeMillis()/1000);
        return generatedId.toString();
    }

}
