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
        String notifi = sharedPreferences.getString("listNotifi", "1");

        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
        {
//            if(notifi.equals("1")) {
//
//            }
//            NotifiSetup.RegisterNotifications(context,null);
        }
    }
}
