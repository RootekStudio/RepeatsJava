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
        boolean isNext = intent.getBooleanExtra("IsNext", false);
        int frequency = 0;
        boolean cannotSend = false;
        Calendar calendar = Calendar.getInstance();

        try {
            if (!isNext) {
                JSONObject rootObject = new JSONObject(JsonFile.readJson(context, "advancedDelivery.json"));
                JSONObject singleCondition = rootObject.getJSONObject(jsonIndex);

                frequency = Integer.parseInt(singleCondition.getString("frequency"));

                JSONArray days = singleCondition.getJSONArray("days");
                ArrayList<String> arrayDays = new ArrayList<>();

                for (int i = 0; i < days.length(); i++) {
                    arrayDays.add(days.getString(i));
                }

                //Nearest day to send notification
                String dayToNotifi = NotificationHelper.checkDays(arrayDays);


                JSONObject hoursObject = singleCondition.getJSONObject("hours");
                Iterator<String> iterator = hoursObject.keys();

                if (dayToNotifi.equals("today")) {
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
                            if (!cannotSend) {
                                break;
                            }

                            if (!iterator.hasNext()) {
                                if (arrayDays.size() > 1) {
                                    arrayDays.remove(String.valueOf(calendar.get(Calendar.DAY_OF_WEEK)));
                                    dayToNotifi = NotificationHelper.checkDays(arrayDays);
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    cannotSend = true;
                }

                if (cannotSend) {

                    Iterator<String> fromIterator = hoursObject.keys();

                    int lowestHour = 24;
                    int lowestMinute = 60;

                    while (fromIterator.hasNext()) {
                        String index = fromIterator.next();

                        JSONObject object = hoursObject.getJSONObject(index);
                        String from = object.getString("from");

                        int fromHour = Integer.parseInt(from.substring(0, 2));
                        int fromMinute = Integer.parseInt(from.substring(3, 5));

                        if (dayToNotifi.equals("today")) {
                            int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
                            int minuteOfDay = calendar.get(Calendar.MINUTE);

                            if (hourOfDay < fromHour) {
                                lowestHour = fromHour;
                                lowestMinute = fromMinute;
                                break;
                            } else if (hourOfDay == fromHour) {
                                if (minuteOfDay < fromMinute) {
                                    lowestHour = fromHour;
                                    lowestMinute = fromMinute;
                                    break;
                                }
                            } else if (!fromIterator.hasNext()) {
                                dayToNotifi = String.valueOf(calendar.get(Calendar.DAY_OF_WEEK));
                            }
                        }

                        if (fromHour < lowestHour) {
                            lowestHour = fromHour;
                            lowestMinute = fromMinute;
                        } else if (fromHour == lowestHour && fromMinute < lowestMinute) {
                            lowestHour = fromMinute;
                        }
                    }
                    NotificationHelper.stopAndRegisterInFuture(dayToNotifi, lowestHour, lowestMinute, context, Integer.parseInt(jsonIndex));
                } else {
                    RepeatsNotificationTemplate.NotifiTemplate(context, false, jsonIndex);
                }
            } else {
                RepeatsNotificationTemplate.NotifiTemplate(context, true, jsonIndex);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (!isNext && !cannotSend) {
            Intent newIntent = new Intent(context, AdvancedTimeNotification.class);
            newIntent.putExtra("jsonIndex", jsonIndex);

            NotificationHelper.registerAdvancedAlarm(context, frequency, newIntent, null, jsonIndex);
        }
    }
}
