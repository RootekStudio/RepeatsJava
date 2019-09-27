package com.rootekstudio.repeatsandroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.rootekstudio.repeatsandroid.notifications.NotifiSetup;

public class OnSystemBoot extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean notifi = sharedPreferences.getBoolean("notifications", false);

        if (notifi && intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
        {
            NotifiSetup.RegisterNotifications(context);
        }
    }
}
