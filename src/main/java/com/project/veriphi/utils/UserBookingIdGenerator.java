package com.project.veriphi.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;

public class UserBookingIdGenerator {

    private static final MessageDigest md5;

    static {
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

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
        byte[] hash = md5.digest(id.toString().getBytes());
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
