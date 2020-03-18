package com.rootekstudio.repeatsandroid.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.RepeatsHelper;
import com.rootekstudio.repeatsandroid.RepeatsSetInfo;
import com.rootekstudio.repeatsandroid.database.DatabaseHelper;

import java.util.List;

public class EnableSetsListActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        RepeatsHelper.DarkTheme(this, false);
        setContentView(R.layout.activity_enable_sets_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final DatabaseHelper DB = new DatabaseHelper(this);
        List<RepeatsSetInfo> AllSets = DB.AllItemsLIST(-1);
        int count = AllSets.size();
        LinearLayout linear = findViewById(R.id.EnableSetsLinear);

        for(int i = 0; i < count; i++)
        {
            RepeatsSetInfo item = AllSets.get(i);
            String name = item.getTableName();
            String isenabled = item.getIsEnabled();
            String title = item.getitle();

            final View view1 = getLayoutInflater().inflate(R.layout.enablesetslistitem, null);
            TextView txtview = view1.findViewById(R.id.SingleSetName);
            Switch SWITCH = view1.findViewById(R.id.EnableSwitch);
            SWITCH.setTag(name);

            SWITCH.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                {
                    if(isChecked)
                    {
                        String NAME = buttonView.getTag().toString();
                        DB.UpdateTable("TitleTable", "IsEnabled='true'", "TableName='" + NAME + "'");
                    }
                    else
                    {
                        String NAME = buttonView.getTag().toString();
                        DB.UpdateTable("TitleTable", "IsEnabled='false'", "TableName='" + NAME + "'");
                    }
                }
            });

            txtview.setText(title);
            if(isenabled.equals("true"))
            {
                SWITCH.setChecked(true);
            }
            else
            {
                SWITCH.setChecked(false);
            }

            linear.addView(view1);
        }

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
}
