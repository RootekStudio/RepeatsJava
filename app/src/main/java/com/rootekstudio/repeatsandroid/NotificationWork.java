package com.rootekstudio.repeatsandroid;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class NotificationWork extends Worker
{
    public NotificationWork(@NonNull Context context, @NonNull WorkerParameters workerParams)
    {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork()
    {
        RepeatsNotificationTemplate.NotifiTemplate(getApplicationContext(), false);
        return Result.success();
    }
}
