package com.rootekstudio.repeatsandroid;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.util.List;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.RemoteInput;

class RepeatsNotificationTemplate
{
    static final String KEY_TEXT_REPLY = "UsersAnswer";

    static void NotifiTemplate(Context context)
    {
        RepeatsHelper.GetQuestionFromDatabase(context);

        String Question = RepeatsHelper.Question;
        String Answer = RepeatsHelper.Answer;
        String tablename = RepeatsHelper.tablename;

        Random random = new Random();
        int rnd = random.nextInt();

        Intent answerActivity = new Intent(context, AnswerActivity.class);
        answerActivity.putExtra("Title", tablename);
        answerActivity.putExtra("Question", Question);
        answerActivity.putExtra("Correct", Answer);
        answerActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,rnd, answerActivity,0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "RepeatsQuestionChannel")
                .setSmallIcon(R.drawable.ic_notifi_icon)
                .setContentTitle(tablename)
                .setContentText(Question)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(Question))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {
            String replyLabel = context.getString(R.string.ReplyText);
            RemoteInput remoteInput = new RemoteInput.Builder(KEY_TEXT_REPLY)
                    .setLabel(replyLabel)
                    .build();

            Intent intent1 = new Intent(context, UserReply.class);
            intent1.putExtra("Correct", Answer);
            PendingIntent replyPendingIntent = PendingIntent.getBroadcast(context,
                    11, intent1, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.drawable.ic_notifi_icon,
                    context.getString(R.string.Reply), replyPendingIntent)
                    .addRemoteInput(remoteInput)
                    .build();

            mBuilder.addAction(action);
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(11, mBuilder.build());
    }

    static void AnswerNotifi(Context context, String Title, String Text)
    {
        Intent intent1 = new Intent(context, RepeatsQuestionSend.class);
        PendingIntent replyPendingIntent = PendingIntent.getBroadcast(context,
                11, intent1, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.drawable.ic_notifi_icon,
                context.getString(R.string.Next), replyPendingIntent)
                .build();

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "RepeatsQuestionChannel")
                .setSmallIcon(R.drawable.ic_notifi_icon)
                .setContentTitle(Title)
                .setContentText(Text)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(Text))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setDefaults(-1)
                .addAction(action);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(11, mBuilder.build());
    }
}
