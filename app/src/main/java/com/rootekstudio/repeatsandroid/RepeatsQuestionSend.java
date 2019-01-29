package com.rootekstudio.repeatsandroid;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.List;
import java.util.Random;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.RemoteInput;

public class RepeatsQuestionSend extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Boolean isNext = intent.getBooleanExtra("IsNext", false);

        RepeatsNotificationTemplate.NotifiTemplate(context, isNext);
    }
}
