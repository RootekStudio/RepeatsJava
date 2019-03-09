package com.rootekstudio.repeatsandroid;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Random;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.RemoteInput;

class RepeatsNotificationTemplate
{
    private static final String KEY_TEXT_REPLY = "UsersAnswer";

    static void NotifiTemplate(Context context, Boolean IsNext)
    {
        NotificationCompat.Builder mBuilder;

        RepeatsHelper.GetQuestionFromDatabase(context);

        String Question = RepeatsHelper.Question;
        String Answer = RepeatsHelper.Answer;
        String tablename = RepeatsHelper.tablename;
        String picturename = RepeatsHelper.PictureName;
        String ignorechars = RepeatsHelper.IgnoreChars;

        Random random = new Random();
        int rnd = random.nextInt();

        Intent answerActivity = new Intent(context, AnswerActivity.class);
        answerActivity.putExtra("Title", tablename);
        answerActivity.putExtra("Question", Question);
        answerActivity.putExtra("Correct", Answer);
        answerActivity.putExtra("Image", picturename);
        answerActivity.putExtra("IgnoreChars", ignorechars);
        answerActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,rnd, answerActivity,0);

        if(IsNext)
        {
            mBuilder = new NotificationCompat.Builder(context, "RepeatsNextChannel");
            mBuilder.setDefaults(Notification.DEFAULT_LIGHTS);
        }
        else
        {
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
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        if(!picturename.equals(""))
        {
            File file = new File(context.getFilesDir(), picturename);
            FileInputStream inputStream = null;
            try
            {
                inputStream = new FileInputStream(file);
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }

            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            mBuilder.setStyle(new NotificationCompat.BigPictureStyle()
                    .bigPicture(bitmap));
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {
            String replyLabel = context.getString(R.string.ReplyText);
            RemoteInput remoteInput = new RemoteInput.Builder(KEY_TEXT_REPLY)
                    .setLabel(replyLabel)
                    .build();

            Intent intent1 = new Intent(context, UserReply.class);
            intent1.putExtra("Correct", Answer);
            intent1.putExtra("IgnoreChars", ignorechars);

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
