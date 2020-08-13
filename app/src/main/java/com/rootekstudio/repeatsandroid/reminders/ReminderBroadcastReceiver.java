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
import java.util.Locale;
import java.util.Scanner;

public class ReminderBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String setsIDs = intent.getStringExtra("setsIDs");
        String setsNames = intent.getStringExtra("setsNames");
        String daysBefore = intent.getStringExtra("daysBefore");

        RepeatsDatabase.getInstance(context).updateReminderEnabled(setsIDs, false);
        List<String> setsNamesList = new ArrayList<>();
        List<String> daysBeforeList = new ArrayList<>();

        Scanner scannerNames = new Scanner(setsNames);
        while (scannerNames.hasNextLine()) {
            setsNamesList.add(scannerNames.nextLine());
        }

        Scanner scannerDays = new Scanner(daysBefore);
        while (scannerDays.hasNextLine()) {
            daysBeforeList.add(scannerDays.nextLine());
        }

        String title;
        String text = "";

        if (daysBeforeList.size() > 1) {
            title = "\u23F0 " + context.getResources().getString(R.string.tests_coming);
            if (Locale.getDefault().toString().equals("pl_PL")) {
                for (int i = 0; i < daysBeforeList.size(); i++) {
                    if (Integer.parseInt(daysBeforeList.get(i)) == 1) {
                        text += context.getResources().getString(R.string.tomorrow) + " " +
                                context.getResources().getString(R.string.test_from_polish_only) + " " +
                                setsNamesList.get(i) + "\n";
                    } else {
                        text += context.getResources().getString(R.string.for_word) + " " + daysBeforeList.get(i) + " " +
                                context.getResources().getString(R.string.days_lower_case) + " " +
                                context.getResources().getString(R.string.test_from_polish_only) + " " +
                                setsNamesList.get(i) + "\n";
                    }
                }
            } else {
                for (int i = 0; i < daysBeforeList.size(); i++) {
                    if (Integer.parseInt(daysBeforeList.get(i)) == 1) {
                        text += setsNamesList.get(i) + " " + context.getResources().getString(R.string.test) + " " +
                                context.getResources().getString(R.string.tomorrow) + "\n";
                    } else {
                        text += setsNamesList.get(i) + " " + context.getResources().getString(R.string.test) + " " +
                                context.getResources().getString(R.string.in_lower_case) + " " + daysBeforeList.get(i) + " " +
                                context.getResources().getString(R.string.days_lower_case) + " " + "\n";
                    }
                }
            }

            text += "\n" + context.getResources().getString(R.string.click_to_learn);
        } else {
            int days = Integer.parseInt(daysBeforeList.get(0));
            if (Locale.getDefault().toString().equals("pl_PL")) {
                if (days > 1) {
                    title = "\u23F0 " + context.getResources().getString(R.string.for_word) + " " +
                            days + " " + context.getResources().getString(R.string.days_lower_case) + " " +
                            context.getResources().getString(R.string.test_from_polish_only) + " " + setsNamesList.get(0);
                } else {
                    title = "\u23F0 " + context.getResources().getString(R.string.tomorrow) + " " +
                            context.getResources().getString(R.string.test_from_polish_only) + " " + setsNamesList.get(0);
                }
            } else {
                if (days > 1) {
                    title = "\u23F0 " + setsNamesList.get(0) + " " + context.getResources().getString(R.string.test) + " " +
                            context.getResources().getString(R.string.in_lower_case) + " " +
                            days + " " + context.getResources().getString(R.string.days_lower_case);
                } else {
                    title = "\u23F0 " + setsNamesList.get(0) + " " + context.getResources().getString(R.string.test) + " " +
                            context.getResources().getString(R.string.tomorrow);
                }
            }
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
        PendingIntent FL = PendingIntent.getActivity(context, RequestCodes.REMINDER_NOTIFICATION_CLICK_ID, fastLearning, PendingIntent.FLAG_CANCEL_CURRENT);
        mBuilder.setContentIntent(FL);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(RequestCodes.REMINDER_NOTIFICATION_ID, mBuilder.build());

        SetReminders.startReminders(context);
    }
}
