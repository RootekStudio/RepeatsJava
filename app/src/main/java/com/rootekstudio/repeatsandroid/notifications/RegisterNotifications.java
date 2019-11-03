package com.rootekstudio.repeatsandroid.notifications;

import android.content.Context;
import android.content.Intent;

import com.rootekstudio.repeatsandroid.JsonFile;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class RegisterNotifications {
    public static void registerAdvancedDelivery(Context context) {
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
