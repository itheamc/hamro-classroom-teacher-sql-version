package com.itheamc.hamroclassroom_teachers.handlers;

import android.util.Log;

import com.itheamc.hamroclassroom_teachers.utils.Amcryption;

import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

import okhttp3.Headers;

public class AuthHandler {
    /*
    Function to generate auth code
     */
    public static Headers authHeaders(String by) {
        String auth = getAuth();
        int day = Calendar.getInstance(Locale.ENGLISH).get(Calendar.MINUTE);
        auth += day;
        Headers.Builder builder = new Headers.Builder();
        builder.add("key1", auth);
        builder.add("key2", Amcryption.getEncoder().encode(auth));
        if (by != null) builder.add("by", by);
        return builder.build();
    }


    private static String getAuth() {
        StringBuilder stringBuilder = new StringBuilder();
        Random random = new Random();
        // For n
        int upperBound = 13;
        int lowerBound = 8;
        int n = random.nextInt(upperBound - lowerBound) + lowerBound;

        for (int i = 0; i < n; i++) {
            int r = random.nextInt(26);
            stringBuilder.append(getLowerChars()[r]);
        }
        return stringBuilder.toString().trim();
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
