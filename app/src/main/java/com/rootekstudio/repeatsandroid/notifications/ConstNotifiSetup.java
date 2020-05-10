package com.rootekstudio.repeatsandroid.notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.preference.PreferenceManager;

import com.rootekstudio.repeatsandroid.OnSystemBoot;
import com.rootekstudio.repeatsandroid.RepeatsHelper;

import java.util.Calendar;

public class ConstNotifiSetup {
    public static void RegisterNotifications(Context cnt, Calendar calendar, int code) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(cnt);
        int time = sharedPreferences.getInt("frequency", 0);

        if (time != 0) {
            long triggerAtMillis;

            if (calendar == null) {
                triggerAtMillis = System.currentTimeMillis() + 1000 * 60 * time;
            } else {
                triggerAtMillis = calendar.getTimeInMillis();
            }

            Intent intent = new Intent(cnt, RepeatsQuestionSend.class);
            intent.putExtra("time", time);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(cnt, code, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) cnt.getSystemService(Context.ALARM_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                        triggerAtMillis,
                        pendingIntent);
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP,
                        triggerAtMillis,
                        pendingIntent);
            }

            ComponentName receiver = new ComponentName(cnt, OnSystemBoot.class);
            PackageManager pm = cnt.getPackageManager();

            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("ListNotifi", "1");
            editor.apply();
        }
    }

    public static void CancelNotifications(Context cnt) {
        Intent intent = new Intent(cnt, RepeatsQuestionSend.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(cnt, RepeatsHelper.staticFrequencyCode, intent, 0);
        AlarmManager alarmManager = (AlarmManager) cnt.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    static void silentRegisterInFuture(int hour, int minute, Context context, int id) {
        Calendar calendarAlarm = Calendar.getInstance();

        Calendar calendarCheck = Calendar.getInstance();
        Calendar calendarNow = Calendar.getInstance();
        calendarCheck.setTimeInMillis(System.currentTimeMillis());
        calendarNow.setTimeInMillis(System.currentTimeMillis());

        calendarCheck.set(Calendar.HOUR_OF_DAY, hour);
        calendarCheck.set(Calendar.MINUTE, minute);
        calendarCheck.set(Calendar.SECOND, 0);

        calendarAlarm.clear();
        calendarAlarm.set(Calendar.HOUR_OF_DAY, hour);
        calendarAlarm.set(Calendar.MINUTE, minute);
        calendarAlarm.set(Calendar.SECOND, 0);

        if (calendarCheck.before(calendarNow) || calendarCheck.equals(calendarNow)) {
            calendarAlarm.add(Calendar.DATE, 1);
        }

        ConstNotifiSetup.CancelNotifications(context);
        ConstNotifiSetup.RegisterNotifications(context, calendarAlarm, id);
    }

    public static void SaveFrequency(Context cnt, int frequency) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(cnt);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("frequency", frequency);
        editor.apply();
    }
}
