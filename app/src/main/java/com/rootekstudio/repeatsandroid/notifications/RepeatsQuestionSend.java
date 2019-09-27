package com.rootekstudio.repeatsandroid.notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;

public class RepeatsQuestionSend extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Boolean isNext = intent.getBooleanExtra("IsNext", false);
        int time = intent.getIntExtra("time", 42);

        RepeatsNotificationTemplate.NotifiTemplate(context, isNext);

        if (!isNext) {
            Intent newIntent = new Intent(context, RepeatsQuestionSend.class);
            newIntent.putExtra("time", time);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 10, newIntent, PendingIntent.FLAG_UPDATE_CURRENT);
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
