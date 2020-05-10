package com.rootekstudio.repeatsandroid;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.documentfile.provider.DocumentFile;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.rootekstudio.repeatsandroid.database.RepeatsDatabase;
import com.rootekstudio.repeatsandroid.database.SaveShared;
import com.rootekstudio.repeatsandroid.database.SingleSetInfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class Backup {

    public static void createBackup(final Context context, final Activity activity) {
        RepeatsDatabase DB = RepeatsDatabase.getInstance(context);
        final MaterialAlertDialogBuilder ALERTbuilder = new MaterialAlertDialogBuilder(context);
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        final View view1 = layoutInflater.inflate(R.layout.where_backup, null);
        ALERTbuilder.setView(view1);
        ALERTbuilder.setTitle(R.string.where_Backup);
        ALERTbuilder.setBackground(context.getDrawable(R.drawable.dialog_shape));
        final AlertDialog alert = ALERTbuilder.show();

        RelativeLayout relCloud = view1.findViewById(R.id.relCloud);
        RelativeLayout relLocal = view1.findViewById(R.id.relLocal);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            relLocal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                    activity.startActivityForResult(intent, RequestCodes.PICK_CATALOG);
                    alert.dismiss();
                }
            });
        } else {
            relLocal.setVisibility(View.INVISIBLE);
        }

        relCloud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.dismiss();
                List<SingleSetInfo> list = DB.allSetsInfo(-1);
                final ArrayList<String> names = new ArrayList<>();
                final ArrayList<String> setsID = new ArrayList<>();

                for (int i = 0; i < list.size(); i++) {
                    SingleSetInfo item = list.get(i);
                    names.add(item.getSetName());
                    setsID.add(item.getSetID());
                }

                final AlertDialog dialog = UIHelper.loadingDialog(context.getString(R.string.loading), activity);

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SetToFile.saveSetsToFile(context, setsID, names);
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
        });
    }

    public static void selectFileToRestore(Context context, Activity activity) {
        Intent zipPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
        zipPickerIntent.setType("application/zip");
        try {
            activity.startActivityForResult(zipPickerIntent, RequestCodes.SELECT_FILE_TO_RESTORE);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, R.string.explorerNotFound, Toast.LENGTH_LONG).show();
        }
    }

    public static void saveBackupLocally(final Context context, Intent data, final Activity activity) {
        Uri selectedUri = data.getData();
        final DocumentFile pickedDir = DocumentFile.fromTreeUri(context, selectedUri);
        String fileName = SetToFile.fileName;

        final AlertDialog dialog = UIHelper.loadingDialog(context.getString(R.string.loading), activity);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                List<SingleSetInfo> list = RepeatsDatabase.getInstance(context).allSetsInfo(-1);
                ArrayList<String> names = new ArrayList<>();
                ArrayList<String> setsID = new ArrayList<>();

                for (int i = 0; i < list.size(); i++) {
                    SingleSetInfo item = list.get(i);
                    names.add(item.getSetName());
                    setsID.add(item.getSetID());
                }

                SetToFile.saveSetsToFile(context, setsID, names);

                DocumentFile docFile = null;
                if (pickedDir != null) {
                    docFile = pickedDir.createFile("application/zip", SetToFile.fileName);
                }
                OutputStream outputStream = null;
                try {
                    outputStream = context.getContentResolver().openOutputStream(docFile.getUri());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                ZipSet.zip(SetToFile.filesToShare, outputStream);

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        Toast.makeText(context, R.string.backup_created, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        thread.start();

    }

    public static void restoreBackup(final Context context, Intent data, final Activity activity) {
        Uri selectedZip = data.getData();
        final AlertDialog dialog = UIHelper.loadingDialog(context.getString(R.string.loading), activity);

        try {
            final InputStream inputStream = context.getContentResolver().openInputStream(selectedZip);

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    ZipSet.UnZip(inputStream, new File(context.getFilesDir(), "shared"));
                    SaveShared.SaveSetsToDB(context, RepeatsDatabase.getInstance(context));

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            Toast.makeText(context, R.string.backup_restored, Toast.LENGTH_LONG).show();
                        }
                    });


                }
            });

            thread.start();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
