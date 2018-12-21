package com.rootekstudio.repeatsandroid;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.List;
import java.util.Random;

public class RepeatsHelper
{
    static String Question;
    static String Answer;
    static String tablename;

    static void GetQuestionFromDatabase(Context context)
    {
        DatabaseHelper DB = new DatabaseHelper(context);
        List<RepeatsListDB> all = DB.AllItemsLIST();
        int count = all.size();

        Random random = new Random();
        int randomint = random.nextInt(count);
        RepeatsListDB single = all.get(randomint);

        tablename = single.getTableName();
        String Title = single.getitle();

        List<RepeatsSingleSetDB> set = DB.AllItemsSET(Title);
        int setcount = set.size();
        Random randomset = new Random();
        int randomsetint = randomset.nextInt(setcount);

        RepeatsSingleSetDB singleSetDB = set.get(randomsetint);
        Question = singleSetDB.getQuestion();
        Answer = singleSetDB.getAnswer();
    }

    static void CancelNotifications(Context cnt)
    {
        Intent intent = new Intent(cnt, RepeatsQuestionSend.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(cnt, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager)cnt.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }
}
