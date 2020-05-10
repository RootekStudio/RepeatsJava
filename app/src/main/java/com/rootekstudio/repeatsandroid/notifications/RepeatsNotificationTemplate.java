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
import com.rootekstudio.repeatsandroid.activities.SettingsActivity;
import com.rootekstudio.repeatsandroid.database.GetQuestion;
import com.rootekstudio.repeatsandroid.database.RepeatsDatabase;
import com.rootekstudio.repeatsandroid.database.Values;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;

public class RepeatsNotificationTemplate {
    private static final String KEY_TEXT_REPLY = "UsersAnswer";
    private static String jsonIndex;

    public static void NotifiTemplate(Context context, Boolean IsNext, String json) {
        NotificationCompat.Builder mBuilder;
        GetQuestion item;
        jsonIndex = json;
        boolean error = false;

        if (json != null) {
            ArrayList<String> setsID = JsonFile.getSelectedSetsIdFromJSON(context, jsonIndex);
            item = new GetQuestion(context, setsID);
        } else {
            item = new GetQuestion(context);
        }

        String Question;
        String Answer = "";
        String tablename;
        String picturename = "";
        int ignorechars = 0;
        String setID = "";
        int itemID = -1;

        if (item.getQuestion() != null) {
            Question = item.getQuestion();
            Answer = item.getAnswer();
            tablename = item.getSetName();
            picturename = item.getPictureName();
            ignorechars = item.getIgnoreChars();
            setID = item.getSetID();
            itemID = item.getItemID();
        } else {
            error = true;
            tablename = context.getString(R.string.cantLoadSet);
            Question = context.getString(R.string.checkSetSettings);
        }

        Random random = new Random();
        int rnd = random.nextInt();

        Intent answerActivity = new Intent(context, AnswerActivity.class);
        answerActivity.putExtra("Title", tablename);
        answerActivity.putExtra("Question", Question);
        answerActivity.putExtra("Correct", Answer);
        answerActivity.putExtra("Image", picturename);
        answerActivity.putExtra("IgnoreChars", ignorechars);
        answerActivity.putExtra("jsonIndex", jsonIndex);
        answerActivity.putExtra("setID", setID);
        answerActivity.putExtra("itemID", itemID);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, rnd, answerActivity, 0);

        if (IsNext) {
            mBuilder = new NotificationCompat.Builder(context, "RepeatsNextChannel");
            mBuilder.setDefaults(Notification.DEFAULT_LIGHTS);
        } else {
            mBuilder = new NotificationCompat.Builder(context, "RepeatsQuestionChannel");
            mBuilder.setDefaults(Notification.DEFAULT_ALL);
        }

        mBuilder
                .setSmallIcon(R.drawable.ic_notifi_icon)
                .setContentTitle(tablename)
                .setContentText(Question)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(Question))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setColor(context.getResources().getColor(R.color.colorAccent))
                .setColorized(true)
                .setAutoCancel(true);

        if (!error) {
            mBuilder.setContentIntent(pendingIntent);
        } else {
            Intent enableSets = new Intent(context, SettingsActivity.class);
            PendingIntent settings = PendingIntent.getActivity(context, rnd, enableSets, 0);
            mBuilder.setContentIntent(settings);
        }

        if (!picturename.equals("")) {
            File file = new File(context.getFilesDir(), picturename);
            FileInputStream inputStream = null;
            try {
                inputStream = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            mBuilder.setStyle(new NotificationCompat.BigPictureStyle()
                    .bigPicture(bitmap));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            String replyLabel = context.getString(R.string.ReplyText);
            RemoteInput remoteInput = new RemoteInput.Builder(KEY_TEXT_REPLY)
                    .setLabel(replyLabel)
                    .build();

            Intent intent1 = new Intent(context, UserReply.class);
            intent1.putExtra("Correct", Answer);
            intent1.putExtra("IgnoreChars", ignorechars);
            intent1.putExtra("setID", setID);
            intent1.putExtra("itemID", itemID);

            PendingIntent replyPendingIntent = PendingIntent.getBroadcast(context,
                    11, intent1, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.drawable.ic_notifi_icon,
                    context.getString(R.string.Reply), replyPendingIntent)
                    .addRemoteInput(remoteInput)
                    .build();

            if (!error) {
                mBuilder.addAction(action);
            }
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(11, mBuilder.build());
    }

    public static void AnswerNotifi(final Context context, String Title, String Text, final boolean goodAnswer, final String setID, final int itemID) {
        new Thread(() -> {
            RepeatsDatabase DB = RepeatsDatabase.getInstance(context);
            if (goodAnswer) {
                DB.increaseValueInSet(setID, itemID, Values.good_answers, 1);
                DB.increaseValueInSetsInfo(setID, Values.good_answers, 1);
            } else {
                DB.increaseValueInSet(setID, itemID, Values.wrong_answers, 1);
                DB.increaseValueInSetsInfo(setID, Values.wrong_answers, 1);
            }
        }).start();

        Intent intent1;
        if (jsonIndex != null) {
            intent1 = new Intent(context, AdvancedTimeNotification.class);
            intent1.putExtra("jsonIndex", jsonIndex);
        } else {
            intent1 = new Intent(context, RepeatsQuestionSend.class);
        }

        intent1.putExtra("IsNext", true);
        PendingIntent replyPendingIntent = PendingIntent.getBroadcast(context,
                11, intent1, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.drawable.ic_notifi_icon,
                context.getString(R.string.Next), replyPendingIntent)
                .build();

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "RepeatsAnswerChannel")
                .setSmallIcon(R.drawable.ic_notifi_icon)
                .setContentTitle(Title)
                .setContentText(Text)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(Text))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setColor(context.getResources().getColor(R.color.colorAccent))
                .setColorized(true)
                .setDefaults(Notification.DEFAULT_LIGHTS)
                .addAction(action);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(11, mBuilder.build());
    }
}
