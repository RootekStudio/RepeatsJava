package com.rootekstudio.repeatsandroid;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.SystemClock;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import java.io.File;
import java.util.List;
import java.util.Random;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.PreferenceManager;

public class RepeatsHelper
{
    static String Question;
    static String Answer;
    static String tablename;
    static String PictureName = "";
    static String IgnoreChars;

    static void GetQuestionFromDatabase(Context context)
    {
        DatabaseHelper DB = new DatabaseHelper(context);
        List<RepeatsListDB> all = DB.ALLEnabledSets();
        int count = all.size();

        Random random = new Random();
        int randomint = random.nextInt(count);
        RepeatsListDB single = all.get(randomint);

        tablename = single.getitle();
        String Title = single.getTableName();
        IgnoreChars = single.getIgnoreChars();

        List<RepeatsSingleSetDB> set = DB.AllItemsSET(Title);
        int setcount = set.size();
        Random randomset = new Random();
        int randomsetint = randomset.nextInt(setcount);

        RepeatsSingleSetDB singleSetDB = set.get(randomsetint);
        Question = singleSetDB.getQuestion();
        Answer = singleSetDB.getAnswer();
        PictureName = singleSetDB.getImag();
    }

    static void CancelNotifications(Context cnt)
    {
        Intent intent = new Intent(cnt, RepeatsQuestionSend.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(cnt, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager)cnt.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(cnt);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("notifications", false);
        editor.apply();
    }

    static void RegisterNotifications(Context cnt)
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(cnt);
        int time = sharedPreferences.getInt("frequency", 0);

        Intent intent = new Intent(cnt, RepeatsQuestionSend.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(cnt, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager)cnt.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + 1000 * 60 * time,
                1000 * 60 * time,
                pendingIntent);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("notifications", true);
        editor.apply();
    }

    static void SaveFrequency(Context cnt, int frequency)
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(cnt);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("frequency", frequency);
        editor.apply();
    }

    static void AskAboutTime(final Context context, final boolean IsSet, final Activity activity)
    {

        AlertDialog.Builder ALERTbuilder = new AlertDialog.Builder(context);

        LayoutInflater layoutInflater = LayoutInflater.from(context);

        final View view1 = layoutInflater.inflate(R.layout.ask, null);
        final EditText editText = view1.findViewById(R.id.EditAsk);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.requestFocus();
        editText.setHint(R.string.enterNumber);
        editText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(4)});
        ALERTbuilder.setView(view1);
        ALERTbuilder.setMessage(R.string.QuestionFreq);
        ALERTbuilder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                if(IsSet)
                {
                    activity.onBackPressed();
                }

            }
        });

        ALERTbuilder.setPositiveButton(R.string.Save, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                    String text = editText.getText().toString();
                    if(!text.equals(""))
                    {
                        int frequency = Integer.parseInt(text);

                        RepeatsHelper.SaveFrequency(context, frequency);
                        RepeatsHelper.CancelNotifications(context);
                        RepeatsHelper.RegisterNotifications(context);
                    }

                    if(IsSet)
                    {
                        activity.onBackPressed();
                    }

            }
        });
        ALERTbuilder.show();
    }

    static Boolean DarkTheme(Context context)
    {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String theme = sharedPreferences.getString("theme", "1");

        if(theme.equals("0"))
        {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            context.setTheme(R.style.AppTheme);
            return false;
        }
        else
        {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            context.setTheme(R.style.DarkAppTheme);
            return true;
        }
    }

    static int calculateInSampleSize (BitmapFactory.Options options, int reqWidth, int reqHeight)
    {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    static void CheckDir(Context cnt)
    {
        File file = new File(cnt.getFilesDir(), "shared");
        if(file.exists())
        {
            String[] files = file.list();
            int count = files.length;
            if (count != 0)
            {
                for(int i = 0; i < count; i++)
                {
                    File toDel = new File(file, files[i]);
                    Boolean delete = toDel.delete();
                }
            }
        }
        else
        {
            Boolean dir = file.mkdir();
        }
    }

    static void whatsNew(Context cnt, boolean request)
    {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(cnt);
        final int version = sharedPreferences.getInt("version", 31);

        if(version < BuildConfig.VERSION_CODE || request)
        {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(cnt);
            alertBuilder.setTitle(R.string.whatsNew);
            alertBuilder.setCancelable(false);
            alertBuilder.setMessage("Repeats "+ BuildConfig.VERSION_NAME + cnt.getString(R.string.updateDescription));
            alertBuilder.setNeutralButton(R.string.close, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("version", BuildConfig.VERSION_CODE);
                    editor.apply();
                }
            });
            AlertDialog dialog = alertBuilder.create();
            dialog.show();
        }
    }
}
