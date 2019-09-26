package com.rootekstudio.repeatsandroid;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.FragmentActivity;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Set;

public class SettingsActivity extends AppCompatActivity
{
    static FragmentActivity activity;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        activity = this;

        context = this;

        RepeatsHelper.DarkTheme(this);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new Preference_Screen())
                .commit();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == android.R.id.home)
        {
            onBackPressed();
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 2) {
            if(resultCode == RESULT_OK) {
                Uri selectedZip = data.getData();
                try {
                    InputStream inputStream = getContentResolver().openInputStream(selectedZip);
                    ZipSet.UnZip(inputStream, new File(getFilesDir(), "shared"));
                    SaveShared.SaveSetsToDB(context, new DatabaseHelper(context));

                    Toast.makeText(context, R.string.backup_restored, Toast.LENGTH_LONG).show();
                }catch(FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        if(requestCode == 3) {
            if(resultCode == RESULT_OK) {
                Uri selectedUri = data.getData();
                DocumentFile pickedDir = DocumentFile.fromTreeUri(this, selectedUri);
                DocumentFile docFile = pickedDir.createFile("application/zip", SetToFile.fileName);

                File file = new File(getFilesDir() + "/shared", SetToFile.fileName);
                Uri fileURI = Uri.fromFile(file);

                try {
                    InputStream inputStream = getContentResolver().openInputStream(fileURI);
                    OutputStream outputStream = this.getContentResolver().openOutputStream(docFile.getUri());

                    BufferedInputStream bis = new BufferedInputStream(inputStream);
                    BufferedOutputStream bos = new BufferedOutputStream(outputStream, 1024);

                    byte[] datas = new byte[1024];
                    int count;
                    while ((count = bis.read(datas, 0, 1024)) != -1)
                    {
                        bos.write(datas, 0, count);
                    }
                } catch(Exception e){
                    e.printStackTrace();
                }

                Toast.makeText(context, R.string.backup_restored, Toast.LENGTH_LONG).show();
            }
        }
    }



}
