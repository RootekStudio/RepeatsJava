package com.rootekstudio.repeatsandroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class OnSystemBoot extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
        {
            RepeatsHelper.RegisterNotifications(context);
        }
    }
}
