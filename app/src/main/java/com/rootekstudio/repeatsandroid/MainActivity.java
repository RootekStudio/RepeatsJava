package com.rootekstudio.repeatsandroid;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity
{
    static boolean IsDark;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        IsDark = RepeatsHelper.DarkTheme(this);
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

        createNotificationChannel();

//        BottomAppBar bottomAppBar = findViewById(R.id.bar);
//        bottomAppBar.replaceMenu();

        DatabaseHelper DB = new DatabaseHelper(this);

        final Intent intent = new Intent(this, RepeatsAddEditActivity.class);
        final LinearLayout listLayout = findViewById(R.id.mainList);
        final LayoutInflater inflater = LayoutInflater.from(this);
        final List<RepeatsListDB> ALL  = DB.AllItemsLIST();
        int ItemsCounts = ALL.size();

        int a = 0;
        for(int i = 0; i < ItemsCounts; i++)
        {
            RepeatsListDB Item = ALL.get(i);

            inflater.inflate(R.layout.mainactivitylistitem, listLayout);
            View view = listLayout.getChildAt(i);

            final RelativeLayout but = view.findViewById(R.id.RelativeMAIN);
            RelativeLayout TakeTest = view.findViewById(R.id.Test);

            String tablename = Item.getTableName();
            String title = Item.getitle();

            if(IsDark)
            {
                but.setBackgroundResource(R.drawable.layout_mainshape_dark);
                TakeTest.setBackgroundResource(R.drawable.layout_buttonshape_dark);
            }

            but.setTag(R.string.Tag_id_0, tablename);
            but.setTag(R.string.Tag_id_1, title);

            TakeTest.setTag(R.string.Tag_id_0, tablename);
            TakeTest.setTag(R.string.Tag_id_1, title);

            TakeTest.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    RelativeLayout button = (RelativeLayout) v;
                    String s0 = button.getTag(R.string.Tag_id_0).toString();
                    String s1 = button.getTag(R.string.Tag_id_1).toString();

                    Intent intent = new Intent(cnt, TestActivity.class);
                    intent.putExtra("TableName", s0);
                    intent.putExtra("title", s1);
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
                    RelativeLayout btn = (RelativeLayout) v;
                    String TITLE = v.getTag(R.string.Tag_id_0).toString();
                    String TABLE_NAME = v.getTag(R.string.Tag_id_1).toString();
                    intent.putExtra("ISEDIT", TITLE);
                    intent.putExtra("NAME", TABLE_NAME);
                    startActivity(intent);
                }
            });
        }

        final FloatingActionButton btn = findViewById(R.id.fab);
        btn.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                intent.putExtra("ISEDIT", "FALSE");
                startActivity(intent);
            }
        });
    }

    private void createNotificationChannel()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            CharSequence name =getString(R.string.ChannelTitle);
            String description = getString(R.string.ChannelDescription);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("RepeatsQuestionChannel", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
