package com.project.veriphi.utils;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class EmailValidator { ;

    public static boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }

        Pattern pattern = Pattern.compile(AppConstants.EMAIL_REGEX);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
