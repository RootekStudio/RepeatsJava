package com.rootekstudio.repeatsandroid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.FileProvider;

import com.rootekstudio.repeatsandroid.settings.SharedPreferencesManager;

import java.io.File;

public class RepeatsHelper {
    public static final String breakLine = "\r\n";
    public static final int staticFrequencyCode = 10000;
    public static String version = "2.7";

    public static void CheckDir(Context cnt) {
        File file = new File(cnt.getFilesDir(), "shared");
        if (file.exists()) {
            String[] files = file.list();
            int count = files.length;
            if (count != 0) {
                for (int i = 0; i < count; i++) {
                    File toDel = new File(file, files[i]);
                    toDel.delete();
                }
            }
        } else {
            file.mkdir();
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
}