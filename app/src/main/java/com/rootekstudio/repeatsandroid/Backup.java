package com.rootekstudio.repeatsandroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.documentfile.provider.DocumentFile;

import com.rootekstudio.repeatsandroid.database.DatabaseHelper;
import com.rootekstudio.repeatsandroid.database.SaveShared;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class Backup {
    public static void createBackup(final Context context, final Activity activity) {
        final AlertDialog.Builder ALERTbuilder = new AlertDialog.Builder(context);
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        final View view1 = layoutInflater.inflate(R.layout.where_backup, null);
        ALERTbuilder.setView(view1);
        ALERTbuilder.setMessage(R.string.where_Backup);
        final AlertDialog alert = ALERTbuilder.show();

        RelativeLayout relCloud = view1.findViewById(R.id.relCloud);
        RelativeLayout relLocal = view1.findViewById(R.id.relLocal);

        final DatabaseHelper DB = new DatabaseHelper(context);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            relLocal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    List<RepeatsListDB> list = DB.AllItemsLIST();
                    ArrayList<String> names = new ArrayList<>();
                    ArrayList<String> setsID = new ArrayList<>();

                    for (int i = 0; i < list.size(); i++) {
                        RepeatsListDB item = list.get(i);
                        names.add(item.getitle());
                        setsID.add(item.getTableName());
                    }

                    SetToFile.saveSetsToFile(context, setsID, names);

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
                List<RepeatsListDB> list = DB.AllItemsLIST();
                ArrayList<String> names = new ArrayList<>();
                ArrayList<String> setsID = new ArrayList<>();

                for (int i = 0; i < list.size(); i++) {
                    RepeatsListDB item = list.get(i);
                    names.add(item.getitle());
                    setsID.add(item.getTableName());
                }

                SetToFile.saveSetsToFile(context, setsID, names);
                RepeatsHelper.shareSets(context, activity);
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

    public static void saveBackupLocally(Context context, Intent data) {
        Uri selectedUri = data.getData();
        DocumentFile pickedDir = DocumentFile.fromTreeUri(context, selectedUri);
        DocumentFile docFile = pickedDir.createFile("application/zip", SetToFile.fileName);

        File file = new File(context.getFilesDir() + "/shared", SetToFile.fileName);

        try {
            OutputStream outputStream = context.getContentResolver().openOutputStream(docFile.getUri());

            ZipSet.zip(SetToFile.filesToShare, outputStream);

            Toast.makeText(context, R.string.backup_created, Toast.LENGTH_LONG).show();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

        public static void restoreBackup (Context context, Intent data){
            Uri selectedZip = data.getData();
            try {
                InputStream inputStream = context.getContentResolver().openInputStream(selectedZip);
                ZipSet.UnZip(inputStream, new File(context.getFilesDir(), "shared"));
                SaveShared.SaveSetsToDB(context, new DatabaseHelper(context));

                Toast.makeText(context, R.string.backup_restored, Toast.LENGTH_LONG).show();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }


    }
