package com.rootekstudio.repeatsandroid;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;
import android.provider.ContactsContract;

import com.rootekstudio.repeatsandroid.DatabaseHelper;
import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.RepeatsListDB;
import com.rootekstudio.repeatsandroid.RepeatsSingleSetDB;

import java.util.List;
import java.util.Random;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class JobNotification extends JobService
{
    @Override
    public boolean onStartJob(JobParameters params)
    {
        for(int i = 1; i > 0; i++)
        {
            DatabaseHelper DB = new DatabaseHelper(this);
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

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "CHANNEL_ID")
                    .setSmallIcon(R.drawable.ic_notifi_icon)
                    .setContentTitle(tablename)
                    .setContentText(Question)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(11, mBuilder.build());

            try
            {
                Thread.sleep(10000);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params)
    {

        return false;
    }
}


