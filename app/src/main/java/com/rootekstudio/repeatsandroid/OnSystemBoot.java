package com.rootekstudio.repeatsandroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.rootekstudio.repeatsandroid.activities.SplashScreenActivity;
import com.rootekstudio.repeatsandroid.notifications.NotificationsScheduler;
import com.rootekstudio.repeatsandroid.reminders.SetReminders;
import com.rootekstudio.repeatsandroid.settings.SharedPreferencesManager;

public class OnSystemBoot extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED") ||
        intent.getAction().equals("android.intent.action.QUICKBOOT_POWERON")) {
            SharedPreferencesManager sharedPreferencesManager = SharedPreferencesManager.getInstance(context);

            if(sharedPreferencesManager.getNotificationsEnabled()) {
                NotificationsScheduler.scheduleNotifications(context);
            }

            if(sharedPreferencesManager.getRemindersEnabled()) {
                SetReminders.startReminders(context);
            }
        }
    }
}
