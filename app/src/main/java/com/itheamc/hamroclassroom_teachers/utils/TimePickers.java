package com.itheamc.hamroclassroom_teachers.utils;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.itheamc.hamroclassroom_teachers.databinding.FragmentSubjectBinding;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Locale;

/**
 * ---------------------------------------------------------------
 * This is the for time picker dialog to choose the start time
 */

public class TimePickers extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    private static final String TAG = "TimePickers";
    private final FragmentSubjectBinding subjectBinding;

    public TimePickers(@NonNull FragmentSubjectBinding subjectBinding) {
        this.subjectBinding = subjectBinding;
    }

    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        NotifyUtils.logDebug(TAG, hourOfDay + ":" + minute);
        EditText timeEdittext = subjectBinding.timeInputLayout.getEditText();
        if (timeEdittext != null) timeEdittext.setText(formatTime(hourOfDay, minute));
    }

    private String formatTime(int h, int m) {
        String ampm = "PM";

        if (h < 12) ampm = "AM";
        if (h > 12) h = h - 12;

        String hour = String.valueOf(h);
        String min = String.valueOf(m);

        if (h < 10) hour = "0" + h;
        if (m < 10) min = "0" + m;


        return String.format(Locale.ENGLISH, "%s:%s %s", hour, min, ampm);
    }
}
