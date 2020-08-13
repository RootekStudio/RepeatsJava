package com.rootekstudio.repeatsandroid.activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.RepeatsHelper;
import com.rootekstudio.repeatsandroid.UIHelper;
import com.rootekstudio.repeatsandroid.settings.SharedPreferencesManager;
import com.rootekstudio.repeatsandroid.firstrun.FirstRunActivity;
import com.rootekstudio.repeatsandroid.mainpage.MainActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class SplashScreenActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        UIHelper.DarkTheme(this, false);

        super.onCreate(savedInstanceState);

        SharedPreferencesManager sharedPreferencesManager = SharedPreferencesManager.getInstance(this);
        Intent intent;
        if (sharedPreferencesManager.getFirstRunTerms() == 0) {
            intent = new Intent(this, FirstRunActivity.class);
        } else {
            intent = new Intent(this, MainActivity.class);
        }

        sharedPreferencesManager.getUserID();

        createNotificationChannels();
        startActivity(intent);
        finish();
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = getSystemService(NotificationManager.class);

            CharSequence name = getString(R.string.ChannelTitle);
            String description = getString(R.string.ChannelDescription);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("RepeatsSendQuestionChannel", name, importance);
            channel.setDescription(description);

            notificationManager.createNotificationChannel(channel);
            notificationManager.deleteNotificationChannel("RepeatsQuestionChannel");

            CharSequence name2 = getString(R.string.channelname2);
            String description2 = getString(R.string.channeldesc2);
            int importance2 = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel2 = new NotificationChannel("RepeatsAnswerChannel", name2, importance2);
            channel2.setDescription(description2);

            notificationManager.createNotificationChannel(channel2);

            CharSequence name3 = getString(R.string.channelname3);
            String description3 = getString(R.string.channeldesc3);
            int importance3 = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel3 = new NotificationChannel("RepeatsNextChannel", name3, importance3);
            channel3.setDescription(description3);

            notificationManager.createNotificationChannel(channel3);

            CharSequence name4 = getString(R.string.channelname4);
            String description4 = getString(R.string.channeldesc4);
            int importance4 = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel4 = new NotificationChannel("RepeatsReadAloudChannel", name4, importance4);
            channel4.setDescription(description4);

            notificationManager.createNotificationChannel(channel4);

            CharSequence name5 = getString(R.string.channelname5);
            String description5 = getString(R.string.channeldesc5);
            int importance5 = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel5 = new NotificationChannel("RepeatsRemindersChannel", name5, importance5);
            channel5.setDescription(description5);

            notificationManager.createNotificationChannel(channel5);
        }
    }
}
