package com.rootekstudio.repeatsandroid.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rootekstudio.repeatsandroid.MainActivityAdapter;
import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.RepeatsHelper;
import com.rootekstudio.repeatsandroid.RepeatsSetInfo;
import com.rootekstudio.repeatsandroid.RequestCodes;
import com.rootekstudio.repeatsandroid.ZipSet;
import com.rootekstudio.repeatsandroid.community.MySetsActivity;
import com.rootekstudio.repeatsandroid.community.RepeatsCommunityStartActivity;
import com.rootekstudio.repeatsandroid.database.DatabaseHelper;
import com.rootekstudio.repeatsandroid.database.SaveShared;
import com.rootekstudio.repeatsandroid.fastlearning.FastLearningConfigActivity;
import com.rootekstudio.repeatsandroid.readaloud.ReadAloudActivity;
import com.rootekstudio.repeatsandroid.statistics.StatsActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Activity activity = null;
    boolean darkTheme;
    String selectedSetID;
    Intent addEditActivityIntent;
    public static List<RepeatsSetInfo> repeatsList;

    RecyclerView recyclerView;
    public static RecyclerView.Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createNotificationChannel();
        RepeatsHelper.askAboutBattery(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        final Context cnt = this;
        activity = this;
        RepeatsHelper.CheckDir(this);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        darkTheme = RepeatsHelper.DarkTheme(this, false);
        getSupportActionBar().setCustomView(R.layout.logo);
        if (!darkTheme) {
            ImageView logo = findViewById(R.id.logoMain);
            logo.setImageResource(R.drawable.repeats_for_light_bg);
        }

        try {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            if(!sharedPreferences.contains("version")) {
                if(sharedPreferences.getInt("firstRunTerms", 3) == 3) {
                    findViewById(R.id.infoLayout).setVisibility(View.VISIBLE);
                }
                else {
                    saveVersion();
                }
            }
            else {
                if(!sharedPreferences.getString("version", "2.5").equals(RepeatsHelper.version)) {
                    findViewById(R.id.infoLayout).setVisibility(View.VISIBLE);
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }


        recyclerView = findViewById(R.id.recycler_view_main);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        DatabaseHelper DB = new DatabaseHelper(this);
        repeatsList = DB.AllItemsLIST();
        mAdapter = new MainActivityAdapter(repeatsList);

        recyclerView.setAdapter(mAdapter);

        final BottomAppBar bottomAppBar = findViewById(R.id.bar);

        bottomAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.app_bar_search) {
                    Intent intent = new Intent(cnt, SearchActivity.class);
                    startActivity(intent);
                } else if (item.getItemId() == R.id.app_bar_settings) {
                    Intent settings = new Intent(cnt, SettingsActivity.class);
                    startActivity(settings);
                } else if (item.getItemId() == R.id.your_sets_rc_button) {
                    Intent intent = new Intent(cnt, MySetsActivity.class);
                    startActivity(intent);
                }
                else if(item.getItemId() == R.id.stats_button) {
                    Intent intent = new Intent(cnt, StatsActivity.class);
                    startActivity(intent);
                }
                return true;
            }
        });

        addEditActivityIntent = new Intent(this, AddEditSetActivity.class);

        final FloatingActionButton btn = findViewById(R.id.fab);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                final MaterialAlertDialogBuilder ALERTbuilder = new MaterialAlertDialogBuilder(cnt);
                LayoutInflater layoutInflater = LayoutInflater.from(cnt);
                final View view1 = layoutInflater.inflate(R.layout.addnew_item, null);
                ALERTbuilder.setView(view1);
                ALERTbuilder.setTitle(R.string.AddSet);
                ALERTbuilder.setBackground(getDrawable(R.drawable.dialog_shape));
                final AlertDialog alert = ALERTbuilder.show();

                RelativeLayout relA = view1.findViewById(R.id.relAdd);
                RelativeLayout relR = view1.findViewById(R.id.relRead);
                RelativeLayout relRC = view1.findViewById(R.id.relRC);

                relA.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addEditActivityIntent.putExtra("ISEDIT", "FALSE");
                        addEditActivityIntent.putExtra("IGNORE_CHARS", "false");
                        alert.dismiss();

                        startActivity(addEditActivityIntent);
                    }
                });

                relR.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent zipPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        zipPickerIntent.setType("application/*");
                        try {
                            startActivityForResult(zipPickerIntent, RequestCodes.READ_SHARED);
                        } catch (ActivityNotFoundException e) {
                            Toast.makeText(cnt, R.string.explorerNotFound, Toast.LENGTH_LONG).show();
                        }
                        alert.dismiss();
                    }
                });

                relRC.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intentRC = new Intent(cnt, RepeatsCommunityStartActivity.class);
                        alert.dismiss();
                        startActivity(intentRC);
                    }
                });
            }
        });

        if (mAdapter.getItemCount() == 0) {
            RelativeLayout r = findViewById(R.id.EmptyHereText);
            r.setVisibility(View.VISIBLE);
        }
    }

    public void setItemClick(View view) {
        String TITLE = view.getTag(R.string.Tag_id_0).toString();
        String TABLE_NAME = view.getTag(R.string.Tag_id_1).toString();
        String IGNORE_CHARS = view.getTag(R.string.Tag_id_2).toString();
        addEditActivityIntent.putExtra("ISEDIT", TITLE);
        addEditActivityIntent.putExtra("NAME", TABLE_NAME);
        addEditActivityIntent.putExtra("IGNORE_CHARS", IGNORE_CHARS);
        startActivity(addEditActivityIntent);
    }

    public void setOptionsClick(View view) {
        selectedSetID = view.getTag().toString();
        RecyclerView recyclerView = (RecyclerView) view.getParent().getParent();
        final int position = recyclerView.getChildAdapterPosition((View) view.getParent());

        PopupMenu popupMenu = new PopupMenu(MainActivity.this, view);
        popupMenu.getMenuInflater().inflate(R.menu.set_options, popupMenu.getMenu());
        MenuPopupHelper menuPopupHelper = new MenuPopupHelper(MainActivity.this, (MenuBuilder) popupMenu.getMenu(), view);
        menuPopupHelper.setForceShowIcon(true);
        menuPopupHelper.show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.fastLearningOption) {
                    Intent intent = new Intent(MainActivity.this, FastLearningConfigActivity.class);
                    intent.putExtra("setID", selectedSetID);
                    startActivity(intent);
                } else if (itemId == R.id.readAloudOption) {
                        Intent intent = new Intent(MainActivity.this, ReadAloudActivity.class);
                        intent.putExtra("setID", selectedSetID);
                        intent.putExtra("newReadAloud", true);
                        startActivity(intent);
                } else if (itemId == R.id.manageSetSettingsOption) {
                    Intent intent = new Intent(MainActivity.this, SetSettingsActivity.class);
                    intent.putExtra("setID", selectedSetID);
                    startActivity(intent);
                }
                return true;
            }
        });
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

    public void closeUpdateInfo(View view) {
        saveVersion();
        RelativeLayout relativeLayout = findViewById(R.id.relativeUpdateInfoRecyclerView);
        relativeLayout.removeView(findViewById(R.id.infoLayout));
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
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.ChannelTitle);
            String description = getString(R.string.ChannelDescription);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("RepeatsQuestionChannel", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            CharSequence name2 = getString(R.string.channelname2);
            String description2 = getString(R.string.channeldesc2);
            int importance2 = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel2 = new NotificationChannel("RepeatsAnswerChannel", name2, importance2);
            channel2.setDescription(description2);

            NotificationManager notificationManager2 = getSystemService(NotificationManager.class);
            notificationManager2.createNotificationChannel(channel2);

            CharSequence name3 = getString(R.string.channelname3);
            String description3 = getString(R.string.channeldesc3);
            int importance3 = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel3 = new NotificationChannel("RepeatsNextChannel", name3, importance3);
            channel3.setDescription(description3);

            NotificationManager notificationManager3 = getSystemService(NotificationManager.class);
            notificationManager3.createNotificationChannel(channel3);

            CharSequence name4 = getString(R.string.channelname4);
            String description4 = getString(R.string.channeldesc4);
            int importance4 = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel4 = new NotificationChannel("RepeatsReadAloudChannel", name4, importance4);
            channel4.setDescription(description4);

            NotificationManager notificationManager4 = getSystemService(NotificationManager.class);
            notificationManager4.createNotificationChannel(channel4);
        }
    }
}
