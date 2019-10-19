package com.rootekstudio.repeatsandroid.notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.SystemClock;

import androidx.preference.PreferenceManager;

import com.rootekstudio.repeatsandroid.RepeatsHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;

public class RepeatsQuestionSend extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Boolean isNext = intent.getBooleanExtra("IsNext", false);
        int time = intent.getIntExtra("time", 42);

        Calendar calendar = GregorianCalendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        JSONObject rootObject = getJSON(context);
        Iterator<String> iterator = rootObject.keys();

        int toHour = 0;
        int toMinute = 0;

        boolean canSend = true;

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean silenceHours = sharedPreferences.getBoolean("silenceHoursSwitch", true);

        if(silenceHours) {
            while (iterator.hasNext()) {
                String index = iterator.next();
                try {
                    JSONObject object = rootObject.getJSONObject(index);
                    String from = object.getString("from");
                    String to = object.getString("to");

                    int fromHour = Integer.parseInt(from.substring(0, 2));
                    toHour = Integer.parseInt(to.substring(0, 2));

                    int fromMinute = Integer.parseInt(from.substring(3, 5));
                    toMinute = Integer.parseInt(to.substring(3, 5));


                    if (fromHour > toHour) {
                        //continue sending notifications
                        if (hour < fromHour && hour > toHour) {
                            continue;
                        }

                        //hour in range - let's check minutes
                        else {
                            //Check if I'm near the set hour
                            if (hour == fromHour) {
                                if (minute >= fromMinute) {
                                    //stop notifications and register new
                                    canSend = false;
                                    break;
                                } else {
                                    //I can still send notification
                                    continue;
                                }
                            }
                            //Check again if I'm near the set hour
                            else if (hour == toHour) {
                                if (minute < toMinute) {
                                    //stop notifications and register new
                                    canSend = false;
                                    break;
                                } else {
                                    //I can send a notification
                                    continue;
                                }
                            }
                            else {
                                canSend = false;
                                break;
                            }
                        }
                    } else if (fromHour < toHour) {
                        //hour in range - stop notifications
                        if (hour >= fromHour && hour <= toHour) {
                            //Check if I'm near the set hour
                            if (hour == fromHour) {
                                if (minute >= fromMinute) {
                                    //stop notifications and register new
                                    canSend = false;
                                    break;
                                } else {
                                    //I can still send notification
                                    continue;
                                }
                            }
                            //Check again if I'm near the set hour
                            else if (hour == toHour) {
                                if (minute < toMinute) {
                                    //stop notifications and register new
                                    canSend = false;
                                    break;
                                } else {
                                    //I can send a notification
                                    continue;
                                }
                            }
                            else {
                                canSend = false;
                                break;
                            }
                        }
                        //hour out of range - continue sending notifications
                        else {
                            continue;
                        }
                    }
                    else if(fromMinute != toMinute){
                        //minute in range
                        if(minute >= fromMinute && minute < toMinute){
                            canSend = false;
                            break;
                        }
                        else if(minute >= fromMinute && fromMinute > toMinute) {
                            canSend = false;
                            break;
                        }
                        else{
                            continue;
                        }
                    }
                    else {
                        continue;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if(canSend) {
                RepeatsNotificationTemplate.NotifiTemplate(context, isNext);
            }
            else {
                stopAndRegisterInFuture(toHour, toMinute, context);
            }
        }
        else {
            RepeatsNotificationTemplate.NotifiTemplate(context, isNext);
        }

        //Schedule next notification
        if (!isNext) {
            Intent newIntent = new Intent(context, RepeatsQuestionSend.class);
            newIntent.putExtra("time", time);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, RepeatsHelper.staticFrequencyCode, newIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        SystemClock.elapsedRealtime() + 1000 * 60 * time,
                        pendingIntent);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        SystemClock.elapsedRealtime() + 1000 * 60 * time,
                        pendingIntent);
            }
        }
    }

    void stopAndRegisterInFuture(int toHour, int toMinute, Context context) {
        Calendar calendarCheck = Calendar.getInstance();
        Calendar calendarNow = Calendar.getInstance();
        calendarCheck.setTimeInMillis(System.currentTimeMillis());
        calendarNow.setTimeInMillis(System.currentTimeMillis());

        calendarCheck.set(Calendar.HOUR_OF_DAY, toHour);
        calendarCheck.set(Calendar.MINUTE, toMinute);
        calendarCheck.set(Calendar.SECOND, 0);

        Calendar calendarAlarm = Calendar.getInstance();
        calendarAlarm.clear();
        calendarAlarm.set(Calendar.HOUR_OF_DAY, toHour);
        calendarAlarm.set(Calendar.MINUTE, toMinute);
        calendarAlarm.set(Calendar.SECOND, 0);

        if(calendarCheck.before(calendarNow) || calendarCheck.equals(calendarNow)) {
            calendarAlarm.add(Calendar.DATE, 1);
        }

        NotifiSetup.CancelNotifications(context);
        NotifiSetup.RegisterNotifications(context, calendarAlarm);
    }

    JSONObject getJSON(Context context) {
        JSONObject rootObject = null;
        try {
            File jsonFile = new File(context.getFilesDir(), "silenceHours.json");
            FileInputStream jsonStream = new FileInputStream(jsonFile);
            BufferedReader jReader = new BufferedReader(new InputStreamReader(jsonStream));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = jReader.readLine()) != null) {
                sb.append(line);
            }

            String fullJSON = sb.toString();

            rootObject = new JSONObject(fullJSON);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return rootObject;
    }
}
