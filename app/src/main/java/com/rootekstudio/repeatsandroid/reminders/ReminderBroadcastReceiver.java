package com.rootekstudio.repeatsandroid.reminders;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.RequestCodes;
import com.rootekstudio.repeatsandroid.database.RepeatsDatabase;
import com.rootekstudio.repeatsandroid.fastlearning.FastLearningConfigActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ReminderBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String setsIDs = intent.getStringExtra("setsIDs");
        String daysBefore = intent.getStringExtra("daysBefore");

        RepeatsDatabase.getInstance(context).updateReminderEnabled(setsIDs, false);
        List<String> setsNamesList = new ArrayList<>();
        List<String> daysBeforeList = new ArrayList<>();

        Scanner scannerIDs = new Scanner(setsIDs);
        while (scannerIDs.hasNextLine()) {
            setsNamesList.add(RepeatsDatabase.getInstance(context).setNameResolver(scannerIDs.nextLine()));
        }

        Scanner scannerDays = new Scanner(daysBefore);
        while (scannerDays.hasNextLine()) {
            daysBeforeList.add(scannerDays.nextLine());
        }

        String title;
        String text = "";

        if (daysBeforeList.size() > 1) {
            title = "\u23F0 " + context.getResources().getString(R.string.tests_coming);
            for (int i = 0; i < daysBeforeList.size(); i++) {
                text += context.getResources().getQuantityString(R.plurals.when_test, Integer.parseInt(daysBeforeList.get(i)),
                        setsNamesList.get(i), Integer.parseInt(daysBeforeList.get(i))) + "\n";
            }
            text += "\n" + context.getResources().getString(R.string.click_to_learn);

        } else {
            int days = Integer.parseInt(daysBeforeList.get(0));
            title = "\u23F0 " + context.getResources().getQuantityString(R.plurals.when_test, days,
                    setsNamesList.get(0), days);
            text = context.getResources().getString(R.string.click_to_learn);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "RepeatsRemindersChannel");
        mBuilder.setDefaults(Notification.DEFAULT_ALL);
        mBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        mBuilder
                .setSmallIcon(R.drawable.ic_notifi_icon)
                .setContentTitle(title)
                .setContentText(text)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(text))
                .setColor(context.getResources().getColor(R.color.colorAccent))
                .setColorized(true)
                .setAutoCancel(true);

        Intent fastLearning = new Intent(context, FastLearningConfigActivity.class);
        fastLearning.putExtra("reminderRequest", setsIDs);
        PendingIntent FL = PendingIntent.getActivity(context, RequestCodes.REMINDER_NOTIFICATION_CLICK_ID, fastLearning, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        mBuilder.setContentIntent(FL);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(RequestCodes.REMINDER_NOTIFICATION_ID, mBuilder.build());

        SetReminders.startReminders(context);
    }
}
