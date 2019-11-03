package com.rootekstudio.repeatsandroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.preference.PreferenceManager;

import com.rootekstudio.repeatsandroid.notifications.AdvancedTimeNotification;
import com.rootekstudio.repeatsandroid.notifications.ConstNotifiSetup;
import com.rootekstudio.repeatsandroid.notifications.NotificationHelper;
import com.rootekstudio.repeatsandroid.notifications.RegisterNotifications;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class OnSystemBoot extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String notifi = sharedPreferences.getString("ListNotifi", "1");

        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            if (notifi.equals("1")) {
                ConstNotifiSetup.RegisterNotifications(context, null, RepeatsHelper.staticFrequencyCode);
            } else if (notifi.equals("2")) {
                RegisterNotifications.registerAdvancedDelivery(context);
            }
        }
    }
}
