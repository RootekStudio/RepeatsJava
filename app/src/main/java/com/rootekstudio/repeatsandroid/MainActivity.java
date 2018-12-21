package com.rootekstudio.repeatsandroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        File file = new File(getFilesDir(), "MigrationCompleted.txt");
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
                    RepeatsHelper.CancelNotifications(cnt);
                }
                return true;
            }
        });

        createNotificationChannel();

//        BottomAppBar bottomAppBar = findViewById(R.id.bar);
//        bottomAppBar.replaceMenu();

        DatabaseHelper DB = new DatabaseHelper(this);

        final Intent intent = new Intent(this, RepeatsAddEditActivity.class);
        final LinearLayout linear = findViewById(R.id.mainLinear);
        final LayoutInflater inflater = LayoutInflater.from(this);
        final List<RepeatsListDB> ALL  = DB.AllItemsLIST();
        int ItemsCounts = ALL.size();

        int a = 0;
        for(int i = 0; i < ItemsCounts; i++)
        {
            inflater.inflate(R.layout.mainactivitylistitem, linear);
            View view = linear.getChildAt(i);
            Button but = view.findViewById(R.id.TempButton);
            but.setText(ALL.get(i).getTableName());
            but.setTag(ALL.get(i).getitle());
            but.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Button btn = (Button) v;
                    String TITLE = v.getTag().toString();
                    String TABLE_NAME = btn.getText().toString();
                    intent.putExtra("ISEDIT", TITLE);
                    intent.putExtra("NAME", TABLE_NAME);
                    startActivity(intent);
                }
            });
        }

        ArrayList<String> List = new ArrayList<String>();

        for(int i = 0; i < ItemsCounts; i++)
        {
            String Name = ALL.get(i).getTableName();
            List.add(Name);
        }

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, List);
//
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
