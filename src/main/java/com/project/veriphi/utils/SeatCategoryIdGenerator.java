package com.project.veriphi.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SeatCategoryIdGenerator {

    private static final MessageDigest md5;

    static {
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String generateId(long eventId, long venueId, String name, String date, String startTime) {
        StringBuilder sb = new StringBuilder(eventId+"-"+venueId+"-"+date+"-"+startTime+"-"+name);
        return compression(sb);
    }
    private static String compression(StringBuilder id) {
        byte[] hash = md5.digest(id.toString().getBytes());
        StringBuilder digest = new StringBuilder();

        for(int i = 0; i<hash.length; i++) {
            if(i%4==0){
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) digest.append('0');
                digest.append(hex);
            }
        }
        return digest.toString();
    }
}
