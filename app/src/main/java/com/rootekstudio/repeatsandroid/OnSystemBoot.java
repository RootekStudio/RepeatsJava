package com.rootekstudio.repeatsandroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.rootekstudio.repeatsandroid.notifications.ConstNotifiSetup;
import com.rootekstudio.repeatsandroid.notifications.RegisterNotifications;

public class OnSystemBoot extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String notifi = SharedPreferencesManager.getInstance(context).getListNotifi();

        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            if (notifi.equals("1")) {
                ConstNotifiSetup.RegisterNotifications(context, null, RepeatsHelper.staticFrequencyCode);
            } else if (notifi.equals("2")) {
                RegisterNotifications.registerAdvancedDelivery(context);
            }
        }
    }
}
