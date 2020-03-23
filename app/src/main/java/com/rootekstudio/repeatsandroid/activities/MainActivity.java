package com.rootekstudio.repeatsandroid.activities;

import android.app.ActionBar;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.rootekstudio.repeatsandroid.Backup;
import com.rootekstudio.repeatsandroid.mainfragments.PreferenceFragment;
import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.RepeatsHelper;
import com.rootekstudio.repeatsandroid.RequestCodes;
import com.rootekstudio.repeatsandroid.ZipSet;
import com.rootekstudio.repeatsandroid.database.DatabaseHelper;
import com.rootekstudio.repeatsandroid.database.SaveShared;
import com.rootekstudio.repeatsandroid.mainfragments.SetsFragment;
import com.rootekstudio.repeatsandroid.mainfragments.StartFragment;
import com.rootekstudio.repeatsandroid.mainfragments.StatsFragment;
import com.rootekstudio.repeatsandroid.readaloud.ReadAloudActivity;
import com.rootekstudio.repeatsandroid.readaloud.ReadAloudConnector;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    boolean darkTheme;
    StartFragment startFragment;
    SetsFragment setsFragment;
    String currectFragment = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RepeatsHelper.createNotificationChannel(this);
        RepeatsHelper.askAboutBattery(this);

        darkTheme = RepeatsHelper.DarkTheme(this, false);
        RepeatsHelper.CheckDir(this);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        getSupportActionBar().setCustomView(R.layout.logo);
        if (!darkTheme) {
            bottomNavigationView.setBackgroundTintList(ContextCompat.getColorStateList(this, android.R.color.white));

            ImageView logo = findViewById(R.id.logoMain);
            logo.setImageResource(R.drawable.repeats_for_light_bg);
        }

        startFragment = new StartFragment();
        setsFragment = new SetsFragment(this, this);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayoutMain, startFragment);
        fragmentTransaction.commit();
        currectFragment = "start";

        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);
    }

    BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            if(item.getItemId() == R.id.startButtonMain) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frameLayoutMain, startFragment);
                fragmentTransaction.commit();
                currectFragment = "start";
            }
            else if(item.getItemId() == R.id.setsButtonMain) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frameLayoutMain, setsFragment);
                fragmentTransaction.commit();
                currectFragment = "sets";
            }
            else if(item.getItemId() == R.id.stats_button) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frameLayoutMain, new StatsFragment());
                fragmentTransaction.commit();
                currectFragment = "stats";
            }
            else if(item.getItemId() == R.id.app_bar_settings) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frameLayoutMain, new PreferenceFragment());
                fragmentTransaction.commit();
                currectFragment = "preferences";
            }
            return true;
        }
    };

    @Override
    public void onResume() {
        super.onResume();

        if(currectFragment.equals("sets")) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frameLayoutMain, new SetsFragment(this, this));
            fragmentTransaction.commit();
        }
    }

    void saveVersion() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("version", RepeatsHelper.version);
        editor.apply();
    }

    public void whatsNewClickMain(View view) {
        saveVersion();
        Intent intent = new Intent(this, WhatsNewActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.searchOption) {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCodes.READ_SHARED) {
            if (resultCode == RESULT_OK) {
                final Context context = this;

                final AlertDialog dialog = RepeatsHelper.showLoadingDialog(context);

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Uri selectedZip = data.getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(selectedZip);
                            ZipSet.UnZip(inputStream, new File(getFilesDir(), "shared"));
                            SaveShared.SaveSetsToDB(context, new DatabaseHelper(context));

                            final Intent intent = new Intent(context, AddEditSetActivity.class);
                            intent.putExtra("ISEDIT", SaveShared.ID);
                            intent.putExtra("NAME", SaveShared.name);
                            intent.putExtra("IGNORE_CHARS", "false");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.dismiss();
                                }
                            });

                            startActivity(intent);

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
            }
        }
        else if(requestCode == RequestCodes.SELECT_FILE_TO_RESTORE) {
            if(resultCode == RESULT_OK) {
                Backup.restoreBackup(this, data, this);
            }
        }
        else if(requestCode == RequestCodes.PICK_CATALOG) {
            if(resultCode == RESULT_OK) {
                Backup.saveBackupLocally(this, data, this);
            }
        }
    }
}
