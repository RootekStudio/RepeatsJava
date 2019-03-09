package com.rootekstudio.repeatsandroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class RepeatsQuestionSend extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Boolean isNext = intent.getBooleanExtra("IsNext", false);

        RepeatsNotificationTemplate.NotifiTemplate(context, isNext);
    }
}
