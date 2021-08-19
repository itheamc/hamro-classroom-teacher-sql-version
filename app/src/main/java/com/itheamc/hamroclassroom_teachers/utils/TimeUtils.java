package com.itheamc.hamroclassroom_teachers.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.Locale;

public class TimeUtils {
    private static final String TAG = "TimeUtils";

    // Converting current time in string and returning it
    public static String now() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            return String.valueOf(Timestamp.from(Instant.now()));
        } else {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
            return dateFormat.format(new Date());
        }
    }

    // Converting current time in string and returning it
    public static String later(long days) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
        return dateFormat.format(new Date(new Date().getTime() + (86400000 * days)));
    }

    // Formatting string time to the date object and returning it
    public static Date toDate(@NonNull String _time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
        Date date = new Date();
        try {
            date = dateFormat.parse(_time);
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e(TAG, "toDate: ", e.getCause());
        }

        return date;
    }

    //
}
