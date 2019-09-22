package com.rootekstudio.repeatsandroid;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.os.SystemClock;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import java.io.File;
import java.util.List;
import java.util.Random;

public class RepeatsHelper {
    static String Question;
    static String Answer;
    static String tablename;
    static String PictureName = "";
    static String IgnoreChars;

    static void GetQuestionFromDatabase(Context context) {
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

    static void CancelNotifications(Context cnt) {
        Intent intent = new Intent(cnt, RepeatsQuestionSend.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(cnt, 10, intent, 0);
        AlarmManager alarmManager = (AlarmManager) cnt.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);

        ComponentName receiver = new ComponentName(cnt, OnSystemBoot.class);
        PackageManager pm = cnt.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(cnt);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("notifications", false);
        editor.apply();
    }

    static void RegisterNotifications(Context cnt) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(cnt);
        int time = sharedPreferences.getInt("frequency", 0);

        if (time != 0) {
            Intent intent = new Intent(cnt, RepeatsQuestionSend.class);
            intent.putExtra("time", time);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(cnt, 10, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) cnt.getSystemService(Context.ALARM_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        SystemClock.elapsedRealtime() + 1000 * 60 * time,
                        pendingIntent);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        SystemClock.elapsedRealtime() + 1000 * 60 * time,
                        pendingIntent);
            } else {
                alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        SystemClock.elapsedRealtime() + 1000 * 60 * time,
                        1000 * 60 * time,
                        pendingIntent);
            }

            ComponentName receiver = new ComponentName(cnt, OnSystemBoot.class);
            PackageManager pm = cnt.getPackageManager();

            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("notifications", true);
            editor.apply();
        }
    }

    static void SaveFrequency(Context cnt, int frequency) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(cnt);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("frequency", frequency);
        editor.apply();
    }

    private static void IfIsSet(Boolean IsSet, Activity activity, Context cnt) {
        if (IsSet) {
            activity.onBackPressed();
        } else {
            activity.finish();
            activity.overridePendingTransition(0, 0);
            cnt.startActivity(activity.getIntent());
            activity.overridePendingTransition(0, 0);
        }
    }

    static void AskAboutTime(final Context context, final boolean IsSet, final Activity activity) {

        AlertDialog.Builder ALERTbuilder = new AlertDialog.Builder(context);

        LayoutInflater layoutInflater = LayoutInflater.from(context);

        final View view1 = layoutInflater.inflate(R.layout.ask, null);
        final EditText editText = view1.findViewById(R.id.EditAsk);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.requestFocus();
        editText.setHint(R.string.enterNumber);
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
        ALERTbuilder.setView(view1);
        ALERTbuilder.setMessage(R.string.QuestionFreq);
        ALERTbuilder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                IfIsSet(IsSet, activity, context);
            }
        });

        ALERTbuilder.setPositiveButton(R.string.Save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = editText.getText().toString();
                if (!text.equals("")) {
                    int frequency = Integer.parseInt(text);

                    RepeatsHelper.SaveFrequency(context, frequency);
                    RepeatsHelper.CancelNotifications(context);
                    RepeatsHelper.RegisterNotifications(context);
                    RepeatsHelper.askAboutBattery(context, IsSet, activity);
                } else {
                    IfIsSet(IsSet, activity, context);
                }
            }
        });
        ALERTbuilder.show();
    }

    static void askAboutBattery(final Context cnt, final Boolean IsSet, final Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            final String packageName = cnt.getPackageName();
            PowerManager pm = (PowerManager) cnt.getSystemService(Context.POWER_SERVICE);

            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(cnt);
                dialog.setTitle(R.string.batteryAskTitle);
                dialog.setMessage(R.string.batteryAskMessage);
                dialog.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(cnt, R.string.CancelOffBattery, Toast.LENGTH_LONG).show();
                        IfIsSet(IsSet, activity, cnt);
                    }
                });

                dialog.setPositiveButton(R.string.Continue, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        IfIsSet(IsSet, activity, cnt);

                        Intent intent = new Intent();
                        intent.setAction(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setData(Uri.parse("package:" + packageName));
                        cnt.startActivity(intent);

                    }
                });

                dialog.show();
            } else {
                IfIsSet(IsSet, activity, cnt);
            }

        } else {
            IfIsSet(IsSet, activity, cnt);
        }
    }

    static Boolean DarkTheme(Context context) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String theme = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            theme = sharedPreferences.getString("theme", "2");
        } else {
            theme = sharedPreferences.getString("theme", "1");
        }

        if (theme.equals("0")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            context.setTheme(R.style.AppTheme);
            return false;
        } else if (theme.equals("1")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            context.setTheme(R.style.DarkAppTheme);
            return true;
        } else {
            Configuration config = context.getApplicationContext().getResources().getConfiguration();
            int currentNightMode = config.uiMode & Configuration.UI_MODE_NIGHT_MASK;
            if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                context.setTheme(R.style.DarkAppTheme);
                return true;
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                context.setTheme(R.style.AppTheme);
                return false;
            }
        }
    }

    static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
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

    static void CheckDir(Context cnt) {
        File file = new File(cnt.getFilesDir(), "shared");
        if (file.exists()) {
            String[] files = file.list();
            int count = files.length;
            if (count != 0) {
                for (int i = 0; i < count; i++) {
                    File toDel = new File(file, files[i]);
                    Boolean delete = toDel.delete();
                }
            }
        } else {
            Boolean dir = file.mkdir();
        }
    }
}