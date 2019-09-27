package com.rootekstudio.repeatsandroid.notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.SystemClock;

import androidx.preference.PreferenceManager;

import com.rootekstudio.repeatsandroid.OnSystemBoot;

public class NotifiSetup {
    public static void RegisterNotifications(Context cnt) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(cnt);
        int time = sharedPreferences.getInt("frequency", 0);

        if (time != 0) {
            Intent intent = new Intent(cnt, RepeatsQuestionSend.class);
            intent.putExtra("time", time);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(cnt, 10, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) cnt.getSystemService(Context.ALARM_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        SystemClock.elapsedRealtime() + 1000 * 60 * time,
                        pendingIntent);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        SystemClock.elapsedRealtime() + 1000 * 60 * time,
                        pendingIntent);
            } else {
                alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        SystemClock.elapsedRealtime() + 1000 * 60 * time,
                        1000 * 60 * time,
                        pendingIntent);
            }

            ComponentName receiver = new ComponentName(cnt, OnSystemBoot.class);
            PackageManager pm = cnt.getPackageManager();

            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("notifications", true);
            editor.apply();
        }
    }

    public static void CancelNotifications(Context cnt) {
        Intent intent = new Intent(cnt, RepeatsQuestionSend.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(cnt, 10, intent, 0);
        AlarmManager alarmManager = (AlarmManager) cnt.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);

        ComponentName receiver = new ComponentName(cnt, OnSystemBoot.class);
        PackageManager pm = cnt.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(cnt);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("notifications", false);
        editor.apply();
    }
}
