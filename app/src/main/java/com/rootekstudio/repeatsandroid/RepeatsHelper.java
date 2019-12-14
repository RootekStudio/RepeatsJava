package com.rootekstudio.repeatsandroid;

import android.app.Activity;
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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.FileProvider;
import androidx.preference.PreferenceManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.rootekstudio.repeatsandroid.notifications.ConstNotifiSetup;

import java.io.File;
import java.util.ArrayList;

public class RepeatsHelper {
    public static final String breakLine = "\r\n";
    public static final int staticFrequencyCode = 10000;
    public static ArrayList<String> setItems;
    public static String setName;

    private static AlertDialog dialog = null;

    static void SaveFrequency(Context cnt, int frequency) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(cnt);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("frequency", frequency);
        editor.apply();
    }

    static void resetActivity(Context cnt, Activity activity) {
        activity.finish();
        activity.overridePendingTransition(0, 0);
        cnt.startActivity(activity.getIntent());
        activity.overridePendingTransition(0, 0);
    }

    public static void AskAboutTime(final Context context, final boolean IsSet, final Activity activity, final Intent intent) {

        MaterialAlertDialogBuilder ALERTbuilder = new MaterialAlertDialogBuilder(context);
        ALERTbuilder.setBackground(context.getDrawable(R.drawable.dialog_shape));
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        final View view1 = layoutInflater.inflate(R.layout.ask, null);
        final EditText editText = view1.findViewById(R.id.EditAsk);
        if(!DarkTheme(context, true)) {
            editText.setBackgroundResource(R.drawable.edittext_shape);
        }
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.requestFocus();
        editText.setHint(R.string.enterNumber);
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});

        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {

                if(keyEvent.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER ) {
                    EditText time = (EditText)view;
                    String timeText = time.getText().toString();
                    if(!timeText.equals("")){
                        saveTime(context, timeText, IsSet, activity, intent);
                    }
                }
                return false;
            }
        });

        ALERTbuilder.setView(view1);
        ALERTbuilder.setTitle(R.string.QuestionFreq);
        ALERTbuilder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(IsSet) {
                    activity.startActivity(intent);
                }
                else {
                    resetActivity(context, activity);
                }
            }
        });

        ALERTbuilder.setPositiveButton(R.string.Save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = editText.getText().toString();
                if (!text.equals("")) {
                    saveTime(context, text, IsSet, activity, intent);
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

    static void saveTime(Context context, String text, Boolean IsSet, Activity activity, Intent intent) {
        int frequency = Integer.parseInt(text);

        RepeatsHelper.SaveFrequency(context, frequency);
        ConstNotifiSetup.CancelNotifications(context);
        ConstNotifiSetup.RegisterNotifications(context, null, RepeatsHelper.staticFrequencyCode);
        //dialog.dismiss();

        resetActivity(context, activity);
    }

    public static void askAboutBattery(final Context cnt) {

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(cnt);
        if(!sharedPreferences.contains("batteryOptimization")) {
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
            if(!onlyCheck) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
            return false;
        } else if (theme.equals("1")) {
            if(!onlyCheck) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
            return true;
        } else {
            Configuration config = context.getApplicationContext().getResources().getConfiguration();
            int currentNightMode = config.uiMode & Configuration.UI_MODE_NIGHT_MASK;
            if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
                if(!onlyCheck) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }
                return true;
            } else {
                if(!onlyCheck) {
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

    public static String removeSpaces(String string){
        if (string.contains(" ")) {
            String testSpace = string.replaceAll(" ", "");
            if (testSpace.length() != 0) {
                while (string.startsWith(" ")) {
                    string = string.substring(1);
                }
                while (string.lastIndexOf(" ") == string.length() - 1) {
                    string = string.substring(0, string.length() - 1);
                }
            }
            else {
                return testSpace;
            }
        }
        return string;
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
        Uri uri = FileProvider.getUriForFile(context, "com.rootekstudio.repeatsandroid.activities.AddEditSetActivity", SetToFile.zipFile);
        Intent share = new Intent();

        share.setAction(Intent.ACTION_SEND);
        share.putExtra(Intent.EXTRA_STREAM, uri);
        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        share.setType("application/zip");
        activity.startActivityForResult(Intent.createChooser(share, context.getString(R.string.send)), RequestCodes.SHARE_SET);
    }
}