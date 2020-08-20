package com.rootekstudio.repeatsandroid.activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.rootekstudio.repeatsandroid.OnSystemBoot;
import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.RepeatsAnalytics;
import com.rootekstudio.repeatsandroid.UIHelper;
import com.rootekstudio.repeatsandroid.firstrun.FirstRunActivity;
import com.rootekstudio.repeatsandroid.mainpage.MainActivity;
import com.rootekstudio.repeatsandroid.settings.SharedPreferencesManager;


public class SplashScreenActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        RepeatsAnalytics.startAnalytics(getApplication());
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

        ComponentName receiver = new ComponentName(this, OnSystemBoot.class);
        PackageManager pm = this.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

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

            notificationManager.deleteNotificationChannel("RepeatsQuestionChannel");
            notificationManager.deleteNotificationChannel("RepeatsAnswerChannel");
            notificationManager.deleteNotificationChannel("RepeatsNextChannel");
        }
    }
}
