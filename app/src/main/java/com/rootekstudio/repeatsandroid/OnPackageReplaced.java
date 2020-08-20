package com.rootekstudio.repeatsandroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.rootekstudio.repeatsandroid.notifications.NotificationsScheduler;
import com.rootekstudio.repeatsandroid.reminders.SetReminders;
import com.rootekstudio.repeatsandroid.settings.SharedPreferencesManager;

public class OnPackageReplaced extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.MY_PACKAGE_REPLACED")) {
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
