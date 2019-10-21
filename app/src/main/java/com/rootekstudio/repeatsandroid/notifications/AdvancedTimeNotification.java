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

public class AdvancedTimeNotification extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String jsonIndex = intent.getStringExtra("jsonIndex");
        Boolean isNext = intent.getBooleanExtra("IsNext", false);
        int frequency = 0;
        ArrayList<String> sets = new ArrayList<>();

        if (!isNext) {
            try {
                JSONObject rootObject = new JSONObject(JsonFile.readJson(context, "advancedDelivery.json"));
                JSONObject singleCondition = rootObject.getJSONObject(jsonIndex);
                frequency = Integer.parseInt(singleCondition.getString("frequency"));

                JSONArray setsArray = singleCondition.getJSONArray("sets");

                for(int i = 0; i < setsArray.length(); i++) {
                    sets.add(setsArray.getString(i));
                }

                JSONArray days = singleCondition.getJSONArray("days");
                ArrayList<String> arrayDays = new ArrayList<>();

                for(int i = 0; i < days.length(); i++) {
                    arrayDays.add(days.getString(i));
                }

                //Nearest day to send notification
                String dayToNotifi = NotificationHelper.checkDays(arrayDays);
                boolean cannotSend = false;

                JSONObject hoursObject = singleCondition.getJSONObject("hours");
                Iterator<String> iterator = hoursObject.keys();

                if(dayToNotifi.equals("today")) {
                    while (iterator.hasNext()) {
                        String index = iterator.next();
                        try {
                            JSONObject object = hoursObject.getJSONObject(index);
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
                    cannotSend = true;
                }

                if(cannotSend) {

                    Calendar calendar = Calendar.getInstance();
                    Iterator<String> fromIterator = hoursObject.keys();

                    int lowestHour = 24;
                    int lowestMinute = 60;

                    while (fromIterator.hasNext()){
                        String index = fromIterator.next();

                        JSONObject object = hoursObject.getJSONObject(index);
                        String from = object.getString("from");

                        int fromHour = Integer.parseInt(from.substring(0, 2));
                        int fromMinute = Integer.parseInt(from.substring(3, 5));

                        if(dayToNotifi.equals("today")){
                            int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
                            int minuteOfDay = calendar.get(Calendar.MINUTE);

                            if(hourOfDay < fromHour){
                                lowestHour = fromHour;
                                lowestMinute = fromMinute;
                                break;
                            }
                            else if(hourOfDay == fromHour) {
                                if(minuteOfDay < fromMinute){
                                    lowestHour = fromHour;
                                    lowestMinute = fromMinute;
                                    break;
                                }
                            }
                            else {
                                dayToNotifi = String.valueOf(calendar.get(Calendar.DAY_OF_WEEK));
                            }
                        }

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
                else {
                    RepeatsNotificationTemplate.NotifiTemplate(context, false, sets);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else {
            RepeatsNotificationTemplate.NotifiTemplate(context, true, sets);
        }

        if(!isNext) {
            Intent newIntent = new Intent(context, AdvancedTimeNotification.class);
            newIntent.putExtra("jsonIndex", jsonIndex);

            NotificationHelper.registerAdvancedAlarm(context, frequency, newIntent, null, jsonIndex);
        }
    }
}
