package com.rootekstudio.repeatsandroid.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.RepeatsHelper;
import com.rootekstudio.repeatsandroid.RepeatsListDB;
import com.rootekstudio.repeatsandroid.RequestCodes;
import com.rootekstudio.repeatsandroid.ZipSet;
import com.rootekstudio.repeatsandroid.database.DatabaseHelper;
import com.rootekstudio.repeatsandroid.database.SaveShared;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
{
    static boolean IsDark;
    Activity activity = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        IsDark = RepeatsHelper.DarkTheme(this, false);
        createNotificationChannel();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(!sharedPreferences.contains("silenceHoursSwitch")) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("silenceHoursSwitch", true);
            editor.apply();
        }

        File jsonFile = new File(getFilesDir(), "silenceHours.json");
        if(!jsonFile.exists()) {
            try {
                JSONObject values = new JSONObject();
                values.put("from","22:00");
                values.put("to", "06:00");

                JSONObject json = new JSONObject();
                json.put("0", values);

                FileWriter fileWriter = new FileWriter(jsonFile);
                String jsonSTRING = json.toString();
                fileWriter.append(jsonSTRING);
                fileWriter.flush();
                fileWriter.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        File jsonAdvanced = new File(getFilesDir(), "advancedDelivery.json");
        if(!jsonAdvanced.exists()) {
            try {
                JSONObject rootObject = new JSONObject();

                JSONObject values = new JSONObject();

                //days
                JSONArray daysArray = new JSONArray();
                DateFormatSymbols symbols = new DateFormatSymbols(Locale.getDefault());
                String[] shortDays = symbols.getShortWeekdays();

                if(Locale.getDefault().getCountry().equals("PL")){
                    for(int i = 2; i <= 7; i++) {
                        daysArray.put(shortDays[i]);
                    }
                    daysArray.put(shortDays[1]);
                }
                else {
                    for(int i = 1; i <= 7; i++) {
                        daysArray.put(shortDays[i]);
                    }
                }
                values.put("days", daysArray);

                //hours
                JSONObject hours = new JSONObject();
                JSONObject singleHour = new JSONObject();

                singleHour.put("from", "22:00");
                singleHour.put("to", "06:00");

                hours.put("0", singleHour);

                values.put("hours", hours);

                //frequency
                values.put("frequency", "30");

                //sets
                JSONArray sets = new JSONArray();
                DatabaseHelper DB = new DatabaseHelper(this);
                ArrayList<String> nameList = DB.getSingleColumn("TableName");
                int setsCount = nameList.size();

                for(int i = 0; i < setsCount; i++) {
                    sets.put(nameList.get(i));
                }
                values.put("sets", sets);

                //put everything to one json
                rootObject.put("0", values);

                FileWriter fileWriter = new FileWriter(jsonAdvanced);
                String jsonSTRING = rootObject.toString();
                fileWriter.write(jsonSTRING);
                fileWriter.flush();
                fileWriter.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        final Context cnt = this;

        activity = this;

        RepeatsHelper.CheckDir(this);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.logo);

        BottomAppBar bottomAppBar = findViewById(R.id.bar);
        bottomAppBar.inflateMenu(R.menu.bottomappbarmain);
        bottomAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                if(item.getItemId() == R.id.app_bar_search)
                {
                    Intent intent = new Intent(cnt, SearchActivity.class);
                    startActivity(intent);
                }
                else if(item.getItemId() == R.id.app_bar_settings)
                {
                    Intent settings = new Intent(cnt, SettingsActivity.class);
                    startActivity(settings);
                }
                return true;
            }
        });

        if(!IsDark)
        {
            bottomAppBar.setBackgroundTint(ContextCompat.getColorStateList(this, R.color.DayColorPrimaryDark));
        }

        DatabaseHelper DB = new DatabaseHelper(this);

        final Intent intent = new Intent(this, AddEditSetActivity.class);
        final LinearLayout listLayout = findViewById(R.id.mainList);
        final LayoutInflater inflater = LayoutInflater.from(this);
        final List<RepeatsListDB> ALL  = DB.AllItemsLIST();
        int ItemsCounts = ALL.size();

        for(int i = 0; i < ItemsCounts; i++)
        {
            RepeatsListDB Item = ALL.get(i);

            inflater.inflate(R.layout.mainactivitylistitem, listLayout);
            View view = listLayout.getChildAt(i);

            final RelativeLayout but = view.findViewById(R.id.RelativeMAIN);
            RelativeLayout TakeTest = view.findViewById(R.id.Test);

            String tablename = Item.getTableName();
            String title = Item.getitle();
            String IgnoreChars = Item.getIgnoreChars();

            if(IsDark)
            {
                but.setBackgroundResource(R.drawable.layout_mainshape_dark);
                TakeTest.setBackgroundResource(R.drawable.layout_buttonshape_dark);
            }

            but.setTag(R.string.Tag_id_0, tablename);
            but.setTag(R.string.Tag_id_1, title);
            but.setTag(R.string.Tag_id_2, IgnoreChars);

            TakeTest.setTag(R.string.Tag_id_0, tablename);
            TakeTest.setTag(R.string.Tag_id_1, title);
            TakeTest.setTag(R.string.Tag_id_2, IgnoreChars);

            TakeTest.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    RelativeLayout button = (RelativeLayout) v;
                    String s0 = button.getTag(R.string.Tag_id_0).toString();
                    String s1 = button.getTag(R.string.Tag_id_1).toString();
                    String s2 = button.getTag(R.string.Tag_id_2).toString();

                    Intent intent = new Intent(cnt, FastLearningActivity.class);
                    intent.putExtra("TableName", s0);
                    intent.putExtra("title", s1);
                    intent.putExtra("IgnoreChars", s2);
                    startActivity(intent);
                }
            });

            TextView Name = view.findViewById(R.id.NameTextView);
            TextView Date = view.findViewById(R.id.DateTextView);

            Name.setText(Item.getitle());
            Date.setText(Item.getCreateDate());

            but.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    String TITLE = v.getTag(R.string.Tag_id_0).toString();
                    String TABLE_NAME = v.getTag(R.string.Tag_id_1).toString();
                    String IGNORE_CHARS = v.getTag(R.string.Tag_id_2).toString();
                    intent.putExtra("ISEDIT", TITLE);
                    intent.putExtra("NAME", TABLE_NAME);
                    intent.putExtra("IGNORE_CHARS", IGNORE_CHARS);
                    startActivity(intent);
                }
            });
        }

        final FloatingActionButton btn = findViewById(R.id.fab);
        btn.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                final AlertDialog.Builder ALERTbuilder = new AlertDialog.Builder(cnt);
                LayoutInflater layoutInflater = LayoutInflater.from(cnt);
                final View view1 = layoutInflater.inflate(R.layout.addnew_item, null);
                ALERTbuilder.setView(view1);

                ALERTbuilder.setMessage(R.string.AddSet);
                final AlertDialog alert = ALERTbuilder.show();

                RelativeLayout relA = view1.findViewById(R.id.relAdd);
                RelativeLayout relR = view1.findViewById(R.id.relRead);

                relA.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        intent.putExtra("ISEDIT", "FALSE");
                        intent.putExtra("IGNORE_CHARS", "false");
                        alert.dismiss();

                        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(cnt);
                        int frequency = sharedPreferences.getInt("frequency", 0);

                        if(frequency == 0) {
                            RepeatsHelper.AskAboutTime(cnt, true, activity, intent);
                        }
                        else {
                            startActivity(intent);
                        }
                    }
                });

                relR.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent zipPickerIntent  = new Intent(Intent.ACTION_GET_CONTENT);
                        zipPickerIntent.setType("application/*");
                        try
                        {
                            startActivityForResult(zipPickerIntent, RequestCodes.READ_SHARED);
                        }
                        catch(ActivityNotFoundException e)
                        {
                            Toast.makeText(cnt, R.string.explorerNotFound, Toast.LENGTH_LONG).show();
                        }
                        alert.dismiss();
                    }
                });
            }
        });

        if(listLayout.getChildCount() == 0)
        {
            RelativeLayout r = findViewById(R.id.EmptyHereText);
            r.setVisibility(View.VISIBLE);
        }
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

                            final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                            int frequency = sharedPreferences.getInt("frequency", 0);

                            if(frequency == 0) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        RepeatsHelper.AskAboutTime(context, true, activity, intent);
                                    }
                                });

                            }
                            else {
                                startActivity(intent);
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
            }
        }
    }

    private void createNotificationChannel()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
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
        }
    }
}
