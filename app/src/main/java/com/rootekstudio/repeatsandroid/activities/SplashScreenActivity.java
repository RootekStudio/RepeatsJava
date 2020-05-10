package com.rootekstudio.repeatsandroid.activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.RepeatsHelper;
import com.rootekstudio.repeatsandroid.firstrun.FirstRunActivity;
import com.rootekstudio.repeatsandroid.mainpage.MainActivity;

import java.util.UUID;

public class SplashScreenActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        RepeatsHelper.DarkTheme(this, false);

        super.onCreate(savedInstanceState);

        Intent intent;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (!sharedPreferences.contains("firstRunTerms")) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("firstRunTerms", 2);
            editor.apply();

            intent = new Intent(this, FirstRunActivity.class);
        } else {
            intent = new Intent(this, MainActivity.class);
        }

        if (!sharedPreferences.contains("userID")) {
            String uniqueID = UUID.randomUUID().toString();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("userID", uniqueID);
            editor.apply();
        }
//
//        AppCenter.start(getApplication(), "347cfec3-4ebc-443c-a9d6-4fdd34df27dd",
//                Analytics.class, Crashes.class);

        createNotificationChannels();
        startActivity(intent);
        finish();
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.ChannelTitle);
            String description = getString(R.string.ChannelDescription);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("RepeatsQuestionChannel", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            CharSequence name2 = getString(R.string.channelname2);
            String description2 = getString(R.string.channeldesc2);
            int importance2 = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel2 = new NotificationChannel("RepeatsAnswerChannel", name2, importance2);
            channel2.setDescription(description2);

            NotificationManager notificationManager2 = getSystemService(NotificationManager.class);
            notificationManager2.createNotificationChannel(channel2);

            CharSequence name3 = getString(R.string.channelname3);
            String description3 = getString(R.string.channeldesc3);
            int importance3 = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel3 = new NotificationChannel("RepeatsNextChannel", name3, importance3);
            channel3.setDescription(description3);

            NotificationManager notificationManager3 = getSystemService(NotificationManager.class);
            notificationManager3.createNotificationChannel(channel3);

            CharSequence name4 = getString(R.string.channelname4);
            String description4 = getString(R.string.channeldesc4);
            int importance4 = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel4 = new NotificationChannel("RepeatsReadAloudChannel", name4, importance4);
            channel4.setDescription(description4);

            NotificationManager notificationManager4 = getSystemService(NotificationManager.class);
            notificationManager4.createNotificationChannel(channel4);
        }
    }
}
