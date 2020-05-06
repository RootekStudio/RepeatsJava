package com.rootekstudio.repeatsandroid;

import android.app.Application;
import android.os.Environment;

import com.rootekstudio.repeatsandroid.database.MigrateDatabase;

import java.io.File;

public class RepeatsApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        migrateDatabase();
    }

    private void migrateDatabase() {
        File file = Environment.getDataDirectory();
        String path = "/data/com.rootekstudio.repeatsandroid/databases/repeats";
        File oldDB = new File(file, path);

        if(oldDB.exists()) {
            new MigrateDatabase(this).migrateToNewDatabase();
            deleteDatabase("repeats");
        }
    }
}
