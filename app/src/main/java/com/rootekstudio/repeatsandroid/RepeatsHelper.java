package com.rootekstudio.repeatsandroid;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
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
import android.text.InputFilter;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.rootekstudio.repeatsandroid.mainpage.PreferenceFragment;
import com.rootekstudio.repeatsandroid.notifications.ConstNotifiSetup;

import java.io.File;
import java.util.HashMap;

public class RepeatsHelper {
    public static final String breakLine = "\r\n";
    public static final int staticFrequencyCode = 10000;
    public static HashMap<Integer, String[]> setItems;
    public static String setName;
    public static String setCreationDate;
    public static String version = "2.7";

    private static AlertDialog dialog = null;

    static void SaveFrequency(Context cnt, int frequency) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(cnt);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("frequency", frequency);
        editor.apply();
    }

    public static void saveVersion(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("version", RepeatsHelper.version);
        editor.apply();
    }

    public static void resetActivity(Context cnt, Activity activity) {
        activity.finish();
        activity.overridePendingTransition(0, 0);
        cnt.startActivity(activity.getIntent());
        activity.overridePendingTransition(0, 0);
    }

    private static void resetFragment(AppCompatActivity activity) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayoutMain, new PreferenceFragment());
        fragmentTransaction.commit();
    }

    public static int getUsableHeight(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowmanager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        assert windowmanager != null;
        windowmanager.getDefaultDisplay().getMetrics(displayMetrics);

        float density = displayMetrics.density;
        float heightDp = displayMetrics.heightPixels / density;
        float relativeHeightDp = heightDp - 50;
        return Math.round(relativeHeightDp * density);
    }

    public static void AskAboutTime(final Context context, final AppCompatActivity activity) {
        MaterialAlertDialogBuilder ALERTbuilder = new MaterialAlertDialogBuilder(context);
        ALERTbuilder.setBackground(context.getDrawable(R.drawable.dialog_shape));
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        final View view1 = layoutInflater.inflate(R.layout.ask, null);
        final EditText editText = view1.findViewById(R.id.EditAsk);
        if (!DarkTheme(context, true)) {
            editText.setBackgroundResource(R.drawable.edittext_shape);
        }
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.requestFocus();
        editText.setHint(R.string.enterNumber);
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});

        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {

                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    EditText time = (EditText) view;
                    String timeText = time.getText().toString();
                    if (!timeText.equals("")) {
                        saveTime(context, timeText, activity);
                    }
                }
                return false;
            }
        });

        ALERTbuilder.setView(view1);
        ALERTbuilder.setTitle(R.string.QuestionFreq);
        ALERTbuilder.setNegativeButton(R.string.Cancel, null);

        ALERTbuilder.setPositiveButton(R.string.Save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = editText.getText().toString();
                if (!text.equals("")) {
                    saveTime(context, text, activity);
                    ComponentName receiver = new ComponentName(context, OnSystemBoot.class);
                    PackageManager pm = context.getPackageManager();

                    pm.setComponentEnabledSetting(receiver,
                            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                            PackageManager.DONT_KILL_APP);
                }
            }
        });

        dialog = ALERTbuilder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();
    }

    static void saveTime(Context context, String text, AppCompatActivity activity) {
        int frequency = Integer.parseInt(text);

        RepeatsHelper.SaveFrequency(context, frequency);
        ConstNotifiSetup.CancelNotifications(context);
        ConstNotifiSetup.RegisterNotifications(context, null, RepeatsHelper.staticFrequencyCode);

        resetFragment(activity);
    }

    public static void askAboutBattery(final Context cnt) {

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(cnt);
        if (!sharedPreferences.contains("batteryOptimization")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                final String packageName = cnt.getPackageName();
                PowerManager pm = (PowerManager) cnt.getSystemService(Context.POWER_SERVICE);

                if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                    MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(cnt);
                    dialog.setBackground(cnt.getDrawable(R.drawable.dialog_shape));
                    dialog.setTitle(R.string.batteryAskTitle);
                    dialog.setMessage(R.string.batteryAskMessage);
                    dialog.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(cnt, R.string.CancelOffBattery, Toast.LENGTH_LONG).show();
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("batteryOptimization", false);
                            editor.apply();

                        }
                    });

                    dialog.setPositiveButton(R.string.Continue, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Intent intent = new Intent();
                            intent.setAction(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setData(Uri.parse("package:" + packageName));
                            cnt.startActivity(intent);

                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("batteryOptimization", true);
                            editor.apply();

                        }
                    });

                    dialog.show();
                }
            }
        }
    }

    public static Boolean DarkTheme(Context context, Boolean onlyCheck) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String theme = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            theme = sharedPreferences.getString("theme", "2");
        } else {
            theme = sharedPreferences.getString("theme", "1");
        }

        if (theme.equals("0")) {
            if (!onlyCheck) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
            return false;
        } else if (theme.equals("1")) {
            if (!onlyCheck) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
            return true;
        } else {
            Configuration config = context.getApplicationContext().getResources().getConfiguration();
            int currentNightMode = config.uiMode & Configuration.UI_MODE_NIGHT_MASK;
            if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
                if (!onlyCheck) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }
                return true;
            } else {
                if (!onlyCheck) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
                return false;
            }
        }
    }

    public static AlertDialog showLoadingDialog(Context context) {
        final MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setBackground(context.getDrawable(R.drawable.dialog_shape));

        final LayoutInflater layoutInflater = LayoutInflater.from(context);
        final View view1 = layoutInflater.inflate(R.layout.progress, null);

        builder.setView(view1);
        builder.setMessage(R.string.loading);
        builder.setCancelable(false);

        ProgressBar progressBar = view1.findViewById(R.id.progressBar);
        progressBar.setIndeterminate(true);

        final AlertDialog dialog = builder.create();
        dialog.show();

        return dialog;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
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

    public static void CheckDir(Context cnt) {
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

    static void shareSets(Context context, Activity activity) {
        Uri uri = FileProvider.getUriForFile(context, "com.rootekstudio.repeatsandroid.fileprovider", SetToFile.zipFile);
        Intent share = new Intent();

        share.setAction(Intent.ACTION_SEND);
        share.putExtra(Intent.EXTRA_STREAM, uri);
        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        share.setType("application/zip");
        activity.startActivityForResult(Intent.createChooser(share, context.getString(R.string.send)), RequestCodes.SHARE_SET);
    }

    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.ChannelTitle);
            String description = context.getString(R.string.ChannelDescription);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("RepeatsQuestionChannel", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            CharSequence name2 = context.getString(R.string.channelname2);
            String description2 = context.getString(R.string.channeldesc2);
            int importance2 = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel2 = new NotificationChannel("RepeatsAnswerChannel", name2, importance2);
            channel2.setDescription(description2);

            NotificationManager notificationManager2 = context.getSystemService(NotificationManager.class);
            notificationManager2.createNotificationChannel(channel2);

            CharSequence name3 = context.getString(R.string.channelname3);
            String description3 = context.getString(R.string.channeldesc3);
            int importance3 = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel3 = new NotificationChannel("RepeatsNextChannel", name3, importance3);
            channel3.setDescription(description3);

            NotificationManager notificationManager3 = context.getSystemService(NotificationManager.class);
            notificationManager3.createNotificationChannel(channel3);

            CharSequence name4 = context.getString(R.string.channelname4);
            String description4 = context.getString(R.string.channeldesc4);
            int importance4 = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel4 = new NotificationChannel("RepeatsReadAloudChannel", name4, importance4);
            channel4.setDescription(description4);

            NotificationManager notificationManager4 = context.getSystemService(NotificationManager.class);
            notificationManager4.createNotificationChannel(channel4);
        }
    }
}