package com.itheamc.hamroclassroom_teachers.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.itheamc.hamroclassroom_teachers.databinding.ActivitySplashBinding;
import com.itheamc.hamroclassroom_teachers.utils.LocalStorage;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";
    private ActivitySplashBinding splashBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        splashBinding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(splashBinding.getRoot());


        new Handler().postDelayed(() -> {
            if (LocalStorage.getInstance(this).isLoggedIn()) {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            } else {
//                LocalStorage.getInstance(this).storeUserId("Sw7njq1zJwabULBL5q2FW1i2ddA3");
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            }
            this.finish();
        }, 500);
    }
}