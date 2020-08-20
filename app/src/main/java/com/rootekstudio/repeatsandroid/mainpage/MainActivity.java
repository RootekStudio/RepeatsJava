package com.rootekstudio.repeatsandroid.mainpage;

import android.app.ActionBar;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.Task;
import com.rootekstudio.repeatsandroid.Backup;
import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.RepeatsAnalytics;
import com.rootekstudio.repeatsandroid.RepeatsHelper;
import com.rootekstudio.repeatsandroid.RequestCodes;
import com.rootekstudio.repeatsandroid.UIHelper;
import com.rootekstudio.repeatsandroid.ZipSet;
import com.rootekstudio.repeatsandroid.activities.AddEditSetActivity;
import com.rootekstudio.repeatsandroid.activities.WhatsNewActivity;
import com.rootekstudio.repeatsandroid.database.MigrateDatabase;
import com.rootekstudio.repeatsandroid.database.RepeatsDatabase;
import com.rootekstudio.repeatsandroid.database.SaveShared;
import com.rootekstudio.repeatsandroid.search.SearchActivity;
import com.rootekstudio.repeatsandroid.settings.SharedPreferencesManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    boolean darkTheme;
    static String currentFragment = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean analyticsEnabled = RepeatsAnalytics.isAnalyticsEnabled();
        if (MigrateDatabase.oldDBExists()) {
            AlertDialog dialog = UIHelper.loadingDialog(getString(R.string.dataMigrate), this);
            dialog.show();

            new Thread(() -> {
                new MigrateDatabase(MainActivity.this).migrateToNewDatabase();
                dialog.cancel();
                startActivity(new Intent(MainActivity.this, MainActivity.class));
                finish();
            }).start();

            return;
        }

        int requestForAppReview = SharedPreferencesManager.getInstance(this).getRequestForAppReview();
        if(requestForAppReview == 3 || requestForAppReview == 10) {

            MaterialAlertDialogBuilder alertDialog = new MaterialAlertDialogBuilder(this);
            alertDialog.setBackground(getDrawable(R.drawable.dialog_shape))
                    .setTitle(R.string.do_you_like_app)
                    .setMessage(R.string.rate_app_in_google_play)
                    .setCancelable(false)
                    .setNegativeButton(R.string.Cancel, (dialogInterface, i) -> {
                        SharedPreferencesManager.getInstance(this).setRequestForAppReview(requestForAppReview + 1);
                    })
                    .setPositiveButton(R.string.rate, (dialogInterface, i) -> {
                        ReviewManager manager = ReviewManagerFactory.create(this);
                        Task<ReviewInfo> request = manager.requestReviewFlow();

                        request.addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                ReviewInfo reviewInfo = task.getResult();

                                Task<Void> flow = manager.launchReviewFlow(this, reviewInfo);
                                flow.addOnCompleteListener(reviewTask -> {});
                            }
                        });

                        SharedPreferencesManager.getInstance(this).setRequestForAppReview(-1);
                    });


            AlertDialog dialog = alertDialog.create();
            dialog.show();
        } else if(requestForAppReview > -1 && requestForAppReview < 10) {
            SharedPreferencesManager.getInstance(this).setRequestForAppReview(requestForAppReview + 1);
        }

        darkTheme = UIHelper.DarkTheme(this, false);
        RepeatsHelper.CheckDir(this);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getTheme();
        theme.resolveAttribute(R.attr.colorSurface, typedValue, true);
        @ColorInt int color = typedValue.data;

        bottomNavigationView.setBackground(new ColorDrawable(color));
        getSupportActionBar().setCustomView(R.layout.logo);
        if (!darkTheme) {
            bottomNavigationView.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            bottomNavigationView.setBackgroundTintList(ContextCompat.getColorStateList(this, android.R.color.white));

            ImageView logo = findViewById(R.id.logoMain);
            logo.setImageResource(R.drawable.repeats_for_light_bg);
        }

        if (currentFragment.equals("") || currentFragment.equals("start")) {
            bottomNavigationView.setSelectedItemId(R.id.startButtonMain);
        } else if (currentFragment.equals("sets")) {
            bottomNavigationView.setSelectedItemId(R.id.setsButtonMain);
        } else if (currentFragment.equals("stats")) {
            bottomNavigationView.setSelectedItemId(R.id.stats_button);
        } else if (currentFragment.equals("preferences")) {
            bottomNavigationView.setSelectedItemId(R.id.app_bar_settings);
        }

        if(getIntent().getBooleanExtra("showSettings", false)) {
            bottomNavigationView.setSelectedItemId(R.id.app_bar_settings);
        }
    }

    BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener = item -> {
        if (item.getItemId() == R.id.startButtonMain) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frameLayoutMain, new StartFragment());
            fragmentTransaction.commit();
            currentFragment = "start";
        } else if (item.getItemId() == R.id.setsButtonMain) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frameLayoutMain, new SetsFragment());
            fragmentTransaction.commit();
            currentFragment = "sets";
        } else if (item.getItemId() == R.id.stats_button) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frameLayoutMain, new StatsFragment());
            fragmentTransaction.commit();
            currentFragment = "stats";
        } else if (item.getItemId() == R.id.app_bar_settings) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frameLayoutMain, new PreferenceFragment());
            fragmentTransaction.commit();
            currentFragment = "preferences";
        }
        return true;
    };

    @Override
    public void onResume() {
        super.onResume();

        if (currentFragment.equals("sets")) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frameLayoutMain, new SetsFragment());
            fragmentTransaction.commit();
        }
    }

    public void whatsNewClickMain(View view) {
        SharedPreferencesManager.getInstance(this).setVersion(RepeatsHelper.version);
        Intent intent = new Intent(this, WhatsNewActivity.class);
        startActivity(intent);
    }

    public void closeUpdateInfo(View view) {
        SharedPreferencesManager.getInstance(this).setVersion(RepeatsHelper.version);
        LinearLayout linearLayout = findViewById(R.id.linearStart);
        linearLayout.removeView(findViewById(R.id.infoLayout));
    }

    public void termsChangedMain(View view) {
        SharedPreferencesManager.getInstance(this).setVersion(RepeatsHelper.version);
        SharedPreferencesManager.getInstance(this).setTermsChanged(RepeatsHelper.termsChanged);
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://rootekstudio.wordpress.com/polityka-prywatnosci")));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.browserNotFound, Toast.LENGTH_LONG).show();
        }
    }

    public void termsChangedClose(View view) {
        SharedPreferencesManager.getInstance(this).setTermsChanged(RepeatsHelper.termsChanged);
        LinearLayout linearLayout = findViewById(R.id.linearStart);
        linearLayout.removeView(findViewById(R.id.termsUpdateLayout));
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

                final AlertDialog dialog = UIHelper.loadingDialog(getString(R.string.loading), this);

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Uri selectedZip = data.getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(selectedZip);
                            ZipSet.UnZip(inputStream, new File(getFilesDir(), "shared"));
                            SaveShared.SaveSetsToDB(context, RepeatsDatabase.getInstance(context));

                            final Intent intent = new Intent(context, AddEditSetActivity.class);
                            intent.putExtra("ISEDIT", SaveShared.ID);
                            intent.putExtra("NAME", SaveShared.name);
                            intent.putExtra("IGNORE_CHARS", "false");
                            runOnUiThread(dialog::dismiss);

                            startActivity(intent);

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
            }
        } else if (requestCode == RequestCodes.SELECT_FILE_TO_RESTORE) {
            if (resultCode == RESULT_OK) {
                Backup.restoreBackup(this, data, this);
            }
        } else if (requestCode == RequestCodes.PICK_CATALOG) {
            if (resultCode == RESULT_OK) {
                Backup.saveBackupLocally(this, data, this);
            }
        }
    }
}
