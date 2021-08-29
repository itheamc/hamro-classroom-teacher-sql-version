package com.itheamc.hamroclassroom_teachers.handlers;

import com.itheamc.hamroclassroom_teachers.utils.Amcryption;

import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

import okhttp3.Headers;

public class AuthHandler {
    /*
    Function to generate auth code
     */
    public static Headers authHeaders() {
        String auth = getAuth();
        int day = Calendar.getInstance(Locale.ENGLISH).get(Calendar.MINUTE);
        auth += day;
        return new Headers.Builder()
                .add("key1", auth)
                .add("key2", Amcryption.getEncoder().encode(auth))
                .build();
    }


    private static String getAuth() {
        StringBuilder stringBuilder = new StringBuilder();
        Random random = new Random();
        // For n
        int upperBound = 15;
        int lowerBound = 10;
        int n = random.nextInt(upperBound - lowerBound) + lowerBound;

        for (int i = 0; i < n; i++) {
            int r = random.nextInt(26);
            stringBuilder.append(getLowerChars()[r]);
        }
        return stringBuilder.toString();
    }

    /* a-z */
    private static char[] getLowerChars() {
        char[] tempLowerChars = new char[26];
        int n = 0;
        for (char c = 'a'; c <= 'z'; c++) {
            tempLowerChars[n] = c;
            n++;
        }

        return tempLowerChars;
    }
}
