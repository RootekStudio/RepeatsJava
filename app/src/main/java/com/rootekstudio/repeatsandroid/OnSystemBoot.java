package com.rootekstudio.repeatsandroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.rootekstudio.repeatsandroid.notifications.AdvancedTimeNotification;
import com.rootekstudio.repeatsandroid.notifications.NotifiSetup;
import com.rootekstudio.repeatsandroid.notifications.NotificationHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class OnSystemBoot extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String notifi = sharedPreferences.getString("listNotifi", "1");

        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
        {
            if(notifi.equals("1")){
                NotifiSetup.RegisterNotifications(context,null, RepeatsHelper.staticFrequencyCode);
            }
            else if(notifi.equals("2")) {
                try {
                    JSONObject advancedFile = new JSONObject(JsonFile.readJson(context, "advancedDelivery.json"));

                    Iterator<String> iterator = advancedFile.keys();

                    while(iterator.hasNext()) {
                        String key = iterator.next();

                        JSONObject singleItem = advancedFile.getJSONObject(key);

                        String freq = singleItem.getString("frequency");

                        Intent newIntent = new Intent(context, AdvancedTimeNotification.class);
                        newIntent.putExtra("jsonIndex", key);

                        NotificationHelper.registerAdvancedAlarm(context, Integer.parseInt(freq), newIntent, null, key);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
