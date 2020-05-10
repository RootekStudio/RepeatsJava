package com.rootekstudio.repeatsandroid;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

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
}
