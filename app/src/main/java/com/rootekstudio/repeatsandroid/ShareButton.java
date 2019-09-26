package com.rootekstudio.repeatsandroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

class ShareButton {
    static void ShareClick(final Context context, final String name, final String setID, final Activity activity) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view1 = layoutInflater.inflate(R.layout.progress, null);
        ProgressBar progressBar = view1.findViewById(R.id.progressBar);
        progressBar.setIndeterminate(true);
        builder.setView(view1);
        builder.setMessage(R.string.savingInProgress);
        builder.setCancelable(false);
        final AlertDialog dialog = builder.create();
        dialog.show();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                ArrayList<String> arrayName = new ArrayList<>();
                arrayName.add(name);

                ArrayList<String> arraySetsID = new ArrayList<>();
                arraySetsID.add(setID);

                SetToFile.saveSetsToFile(context, arraySetsID, arrayName);

                RepeatsHelper.shareSets(context, activity);

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                    }
                });
            }
        });

        thread.start();

    }
}
