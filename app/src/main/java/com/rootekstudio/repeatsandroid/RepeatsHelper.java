package com.rootekstudio.repeatsandroid;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import java.util.List;
import java.util.Random;

import androidx.appcompat.app.AppCompatDelegate;
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

    static void AskAboutTime(final Context context, final boolean IsSet, final FragmentActivity activity)
    {

        AlertDialog.Builder ALERTbuilder = new AlertDialog.Builder(context);

        LayoutInflater layoutInflater = LayoutInflater.from(context);

        final View view1 = layoutInflater.inflate(R.layout.ask, null);
        final EditText editText = view1.findViewById(R.id.EditAsk);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
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
            return false;
        }
        else
        {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            context.setTheme(R.style.DarkAppTheme);
            return true;
        }
    }
}
