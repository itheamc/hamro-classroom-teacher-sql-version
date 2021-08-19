package com.itheamc.hamroclassroom_teachers.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

public class SpinnerAdapter extends ArrayAdapter<String> {

    public SpinnerAdapter(@NonNull Context context, int resource, @NonNull String[] objects) {
        super(context, resource, objects);
        this.setDropDownViewResource(resource);
    }
}
