package com.rootekstudio.repeatsandroid;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity
{
    static boolean IsDark;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        IsDark = RepeatsHelper.DarkTheme(this);
        createNotificationChannel();
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        IsDark = RepeatsHelper.DarkTheme(this);
        File file = new File(getFilesDir(), "SetsMigrationCompleted.txt");
        File file2 = new File(getFilesDir(), "ProjectsName.txt");

        final Context cnt = this;

        if(!file.exists() && file2.exists())
        {
            SetsMigrationTool.MigrateFromOldVersion(this);
        }

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

        final Intent intent = new Intent(this, RepeatsAddEditActivity.class);
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

                    Intent intent = new Intent(cnt, TestActivity.class);
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
                        startActivity(intent);
                        alert.dismiss();
                    }
                });

                relR.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {

                        RepeatsHelper.CheckDir(cnt);

                        Intent zipPickerIntent  = new Intent(Intent.ACTION_GET_CONTENT);
                        zipPickerIntent.setType("application/zip");
                        startActivityForResult(zipPickerIntent, 1);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == 1)
        {
            if (resultCode == RESULT_OK)
            {
                Uri selectedZip = data.getData();
                try
                {
                    InputStream inputStream = getContentResolver().openInputStream(selectedZip);
                    ShareSet.UnZip(inputStream, new File(getFilesDir(), "shared"));
                    Intent intent = new Intent(this, RepeatsAddEditActivity.class);
                    intent.putExtra("ISEDIT", "FALSE");
                    intent.putExtra("IGNORE_CHARS", "false");
                    intent.putExtra("LoadShared", true);
                    startActivity(intent);
                }
                catch (FileNotFoundException e)
                {
                    e.printStackTrace();
                }
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
