package com.rootekstudio.repeatsandroid.notifications;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.RemoteInput;

import com.rootekstudio.repeatsandroid.JsonFile;
import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.RequestCodes;
import com.rootekstudio.repeatsandroid.database.GetQuestion;
import com.rootekstudio.repeatsandroid.database.RepeatsDatabase;
import com.rootekstudio.repeatsandroid.database.Values;
import com.rootekstudio.repeatsandroid.mainpage.MainActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;

public class RepeatsNotificationTemplate {
    public static void questionNotification(Context context, GetQuestion getQuestion, String setsIDs, boolean isNext) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, "RepeatsSendQuestionChannel");
        notificationBuilder.setDefaults(Notification.DEFAULT_ALL);
        notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        notificationBuilder
                .setSmallIcon(R.drawable.ic_notifi_icon)
                .setContentTitle(getQuestion.getSetName())
                .setContentText(getQuestion.getQuestion())
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(getQuestion.getQuestion()))
                .setColor(context.getResources().getColor(R.color.colorAccent))
                .setColorized(true)
                .setAutoCancel(true)
                .setOnlyAlertOnce(isNext);

        if (!getQuestion.getPictureName().equals("")) {
            File file = new File(context.getFilesDir(), getQuestion.getPictureName());
            FileInputStream inputStream = null;
            try {
                inputStream = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            notificationBuilder.setStyle(new NotificationCompat.BigPictureStyle()
                    .bigPicture(bitmap));
        }

        Intent answerActivity = new Intent(context, AnswerActivity.class);
        answerActivity.putExtra("Title", getQuestion.getSetName());
        answerActivity.putExtra("Question", getQuestion.getQuestion());
        answerActivity.putExtra("Correct", getQuestion.getAnswer());
        answerActivity.putExtra("Image", getQuestion.getPictureName());
        answerActivity.putExtra("IgnoreChars", getQuestion.getIgnoreChars());
        answerActivity.putExtra("setID", getQuestion.getSetID());
        answerActivity.putExtra("setsIDs", setsIDs);
        answerActivity.putExtra("itemID", getQuestion.getItemID());
        PendingIntent notificationClickPendingIntent = PendingIntent.getActivity(context, RequestCodes.PENDING_INTENT_QUESTION_NOTIFICATION_CLICK_REQUEST_CODE,
                answerActivity, PendingIntent.FLAG_CANCEL_CURRENT);

        notificationBuilder.setContentIntent(notificationClickPendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            String replyLabel = context.getString(R.string.ReplyText);
            RemoteInput remoteInput = new RemoteInput.Builder("UsersAnswer")
                    .setLabel(replyLabel)
                    .build();

            Intent userReplyIntent = new Intent(context, UserReply.class);
            userReplyIntent.putExtra("Correct", getQuestion.getAnswer());
            userReplyIntent.putExtra("IgnoreChars", getQuestion.getIgnoreChars());
            userReplyIntent.putExtra("setID", getQuestion.getSetID());
            userReplyIntent.putExtra("setsIDs", setsIDs);
            userReplyIntent.putExtra("itemID", getQuestion.getItemID());

            PendingIntent replyPendingIntent = PendingIntent.getBroadcast(context,
                    RequestCodes.PENDING_INTENT_USER_REPLY_REQUEST_CODE, userReplyIntent, PendingIntent.FLAG_CANCEL_CURRENT);

            NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.drawable.ic_notifi_icon,
                    context.getString(R.string.Reply), replyPendingIntent)
                    .addRemoteInput(remoteInput)
                    .build();

            notificationBuilder.addAction(action);
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(RequestCodes.QUESTION_NOTIFICATION_ID, notificationBuilder.build());
    }

    public static void AnswerNotifi(Context context, String Title, String Text, String setsIDs) {
        Intent nextQuestionIntent = new Intent(context, NextQuestionBroadcastReceiver.class);
        nextQuestionIntent.putExtra("setsIDs", setsIDs);

        PendingIntent replyPendingIntent = PendingIntent.getBroadcast(context,
                RequestCodes.PENDING_INTENT_NEXT_QUESTION_REQUEST_CODE, nextQuestionIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.drawable.ic_notifi_icon,
                context.getString(R.string.Next), replyPendingIntent)
                .build();

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "RepeatsSendQuestionChannel")
                .setSmallIcon(R.drawable.ic_notifi_icon)
                .setContentTitle(Title)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(Text))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setColor(context.getResources().getColor(R.color.colorAccent))
                .setColorized(true)
                .setDefaults(Notification.DEFAULT_LIGHTS)
                .setOnlyAlertOnce(true)
                .addAction(action);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(RequestCodes.QUESTION_NOTIFICATION_ID, mBuilder.build());
    }
}
