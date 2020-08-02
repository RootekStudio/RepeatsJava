package com.rootekstudio.repeatsandroid;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.rootekstudio.repeatsandroid.settings.SharedPreferencesManager;

public class UIHelper {
    public static AlertDialog loadingDialog(String text, Activity activity) {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(activity);
        dialogBuilder.setBackground(activity.getDrawable(R.drawable.dialog_shape));

        View view = LayoutInflater.from(activity).inflate(R.layout.loading, null);
        TextView textView = view.findViewById(R.id.loadingText);
        textView.setText(text);

        dialogBuilder.setView(view);
        dialogBuilder.setCancelable(false);
        return dialogBuilder.show();
    }

    public static void restartActivity(Activity activity) {
        activity.finish();
        activity.overridePendingTransition(0, 0);
        activity.startActivity(activity.getIntent());
        activity.overridePendingTransition(0, 0);
    }

    public static Boolean DarkTheme(Context context, Boolean onlyCheck) {
        String theme = SharedPreferencesManager.getInstance(context).getTheme();

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
}
