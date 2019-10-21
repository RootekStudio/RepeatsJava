package com.rootekstudio.repeatsandroid.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.rootekstudio.repeatsandroid.JsonFile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

public class RegisterAdvancedTimeNotification extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String jsonIndex = intent.getStringExtra("jsonIndex");

        try {
            JSONObject rootObject = new JSONObject(JsonFile.readJson(context, "advancedDelivery.json"));
            JSONObject singleCondition = rootObject.getJSONObject(jsonIndex);

            JSONArray days = singleCondition.getJSONArray("days");
            ArrayList<String> arrayDays = new ArrayList<>();

            for(int i = 0; i < days.length(); i++) {
                arrayDays.add(days.getString(i));
            }

            //Nearest day to send notification
            String dayToNotifi = NotificationHelper.checkDays(arrayDays);
            boolean cannotSend = true;

            JSONObject hoursObject = singleCondition.getJSONObject("hours");
            Iterator<String> iterator = hoursObject.keys();

            if(dayToNotifi.equals("today")) {
                while (iterator.hasNext()) {
                    String index = iterator.next();
                    try {
                        JSONObject object = rootObject.getJSONObject(index);
                        String from = object.getString("from");
                        String to = object.getString("to");

                        int fromHour = Integer.parseInt(from.substring(0, 2));
                        int toHour = Integer.parseInt(to.substring(0, 2));

                        int fromMinute = Integer.parseInt(from.substring(3, 5));
                        int toMinute = Integer.parseInt(to.substring(3, 5));

                        cannotSend = NotificationHelper.checkHours(fromHour, toHour, fromMinute, toMinute);
                        if(!cannotSend) {
                            break;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            else {
                cannotSend = false;
            }

            if(!cannotSend) {
                RepeatsNotificationTemplate.NotifiTemplate(context, false);
            }
            else {
                Iterator<String> fromIterator = hoursObject.keys();

                int lowestHour = 24;
                int lowestMinute = 60;

                while (iterator.hasNext()){
                    String index = fromIterator.next();

                    JSONObject object = rootObject.getJSONObject(index);
                    String from = object.getString("from");

                    int fromHour = Integer.parseInt(from.substring(0, 2));
                    int fromMinute = Integer.parseInt(from.substring(3, 5));

                    if(fromHour < lowestHour) {
                        lowestHour = fromHour;
                        lowestMinute = fromMinute;
                    }
                    else if(fromHour == lowestHour && fromMinute < lowestMinute) {
                        lowestHour = fromMinute;
                    }
                }
                NotificationHelper.stopAndRegisterInFuture(dayToNotifi, lowestHour, lowestMinute, context, Integer.parseInt(jsonIndex));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}
