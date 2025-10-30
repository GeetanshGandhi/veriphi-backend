package com.project.veriphi.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AppConstants {


    public static final MessageDigest MD5;

    static {
        try {
            MD5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static final ObjectMapper OBJECT_MAPPER= new ObjectMapper();

    public static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    public static final String GROUP_APPROVED_STATUS = "Approved";
    public static final String GROUP_REJECTED_STATUS = "Rejected";
    public static final String GROUP_PENDING_STATUS = "Pending";
    public static final String BINDING_SERVICE_URL = "https://veriphi-image-scanning.onrender.com/qr_generation";


    public static int USER_EXPIRATION_TIME = 15 * 60;
    public static int TRIAL_COUNT = 5;
}
