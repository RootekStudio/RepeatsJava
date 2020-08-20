package com.rootekstudio.repeatsandroid.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.rootekstudio.repeatsandroid.database.GetQuestion;

public class NotificationBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String setsIDs = intent.getStringExtra("setsIDs");
        GetQuestion getQuestion = new GetQuestion(context, setsIDs);

        if(getQuestion.getQuestion() != null) {
            RepeatsNotificationTemplate.questionNotification(context, getQuestion, setsIDs, false);
        }

        NotificationsScheduler.scheduleNotifications(context);
    }
}
