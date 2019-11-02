package com.rootekstudio.repeatsandroid;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.rootekstudio.repeatsandroid.activities.SettingsActivity;
import com.rootekstudio.repeatsandroid.notifications.AdvancedTimeNotification;
import com.rootekstudio.repeatsandroid.notifications.NotifiSetup;
import com.rootekstudio.repeatsandroid.notifications.NotificationHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class RegisterNotifications {
    public static void registerConstFrequency(Context context) {

        NotifiSetup.RegisterNotifications(context,null, RepeatsHelper.staticFrequencyCode);
    }

    public static void registerAdvanedDelivery(Context context) {
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
