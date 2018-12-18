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
    public static final String KEY_TEXT_REPLY = "UsersAnswer";
    @Override
    public void onReceive(Context context, Intent intent)
    {
        String replyLabel = context.getResources().getString(R.string.ReplyText);
        RemoteInput remoteInput = new RemoteInput.Builder(KEY_TEXT_REPLY)
                .setLabel(replyLabel)
                .build();

//        PendingIntent replyPendingIntent = PendingIntent.getBroadcast(context,
//                11, getMessageReplyIntent(11))

        DatabaseHelper DB = new DatabaseHelper(context);
        List<RepeatsListDB> all = DB.AllItemsLIST();
        int count = all.size();

        Random random = new Random();
        int randomint = random.nextInt(count);
        RepeatsListDB single = all.get(randomint);

        String tablename = single.getTableName();
        String Title = single.getitle();

        List<RepeatsSingleSetDB> set = DB.AllItemsSET(Title);
        int setcount = set.size();
        Random randomset = new Random();
        int randomsetint = randomset.nextInt(setcount);

        RepeatsSingleSetDB singleSetDB = set.get(randomsetint);
        String Question = singleSetDB.getQuestion();

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "RepeatsQuestionChannel")
                .setSmallIcon(R.drawable.ic_notifi_icon)
                .setContentTitle(tablename)
                .setContentText(Question)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(Question))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(11, mBuilder.build());
    }
}
