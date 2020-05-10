package com.rootekstudio.repeatsandroid.notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;

import com.rootekstudio.repeatsandroid.JsonFile;
import com.rootekstudio.repeatsandroid.RepeatsHelper;
import com.rootekstudio.repeatsandroid.SharedPreferencesManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class RepeatsQuestionSend extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isNext = intent.getBooleanExtra("IsNext", false);
        int time = intent.getIntExtra("time", 42);

        SharedPreferencesManager sharedPreferencesManager = SharedPreferencesManager.getInstance(context);

        String notifiMode = sharedPreferencesManager.getListNotifi();
        boolean silenceHours = sharedPreferencesManager.getSilenceHours();
        boolean canSend = true;

        if (isNext) {
            RepeatsNotificationTemplate.NotifiTemplate(context, true, null);
        } else if (notifiMode.equals("1")) {
            if (silenceHours) {
                JSONObject rootObject = null;
                try {
                    rootObject = new JSONObject(JsonFile.readJson(context, "silenceHours.json"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Iterator<String> iterator = rootObject.keys();

                int toHour = 0;
                int toMinute = 0;

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

                        canSend = NotificationHelper.checkHours(fromHour, toHour, fromMinute, toMinute);
                        if (!canSend) {
                            break;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (canSend) {
                    RepeatsNotificationTemplate.NotifiTemplate(context, false, null);
                } else {
                    ConstNotifiSetup.silentRegisterInFuture(toHour, toMinute, context, 12345);
                }
            } else {
                RepeatsNotificationTemplate.NotifiTemplate(context, false, null);
            }
        } else {
            RepeatsNotificationTemplate.NotifiTemplate(context, false, null);
        }

        //Schedule next notification
        if (!isNext && canSend) {
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
}
