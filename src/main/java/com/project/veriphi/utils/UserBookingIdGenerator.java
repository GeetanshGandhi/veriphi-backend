package com.project.veriphi.utils;

import java.time.Instant;

public class UserBookingIdGenerator {

    public static String createBookingID(String userEmail){
        String user = userEmail.split("@")[0];
        StringBuilder generatedId = new StringBuilder();
        for(int i = 1; i<user.length(); i+=2)
            if(Character.isLetterOrDigit(user.charAt(i)))
                generatedId.append(user.charAt(i));
        for(int i = 0; i<user.length(); i+=2)
            if(Character.isLetterOrDigit(user.charAt(i)))
                generatedId.append(user.charAt(i));
        String time = Instant.now().toString();
        for(char ch: time.toCharArray()) {
            if(Character.isDigit(ch)) generatedId.append(ch);
        }
        return compression(generatedId);
    }

    private static String compression(StringBuilder id) {
        byte[] hash = AppConstants.MD5.digest(id.toString().getBytes());
        StringBuilder digest = new StringBuilder();

        for(int i = 0; i<hash.length; i++) {
            if(i%2==0){
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) digest.append('0');
                digest.append(hex);
            }
        }
        return digest.toString();
    }
}
