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
import com.rootekstudio.repeatsandroid.database.RepeatsDatabase;
import com.rootekstudio.repeatsandroid.database.SingleSetInfo;
import com.rootekstudio.repeatsandroid.database.Values;

import java.util.List;

public class EnableSetsListActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RepeatsHelper.DarkTheme(this, false);
        setContentView(R.layout.activity_enable_sets_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final RepeatsDatabase DB = new RepeatsDatabase(this);
        List<SingleSetInfo> AllSets = DB.allSetsInfo(-1);
        int count = AllSets.size();
        LinearLayout linear = findViewById(R.id.EnableSetsLinear);

        for (int i = 0; i < count; i++) {
            SingleSetInfo item = AllSets.get(i);
            String name = item.getSetName();
            int isEnabled = item.getIsEnabled();
            String title = item.getSetName();

            final View view1 = getLayoutInflater().inflate(R.layout.enablesetslistitem, null);
            TextView txtview = view1.findViewById(R.id.SingleSetName);
            Switch SWITCH = view1.findViewById(R.id.EnableSwitch);
            SWITCH.setTag(name);

            SWITCH.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        String NAME = buttonView.getTag().toString();
                        DB.updateTable(Values.sets_info, Values.enabled + "=1", Values.set_id + "='" + NAME + "'");
                    } else {
                        String NAME = buttonView.getTag().toString();
                        DB.updateTable(Values.sets_info, Values.enabled + "=0", Values.set_id + "='" + NAME + "'");
                    }
                }
            });

            txtview.setText(title);
            if (isEnabled == 1) {
                SWITCH.setChecked(true);
            } else {
                SWITCH.setChecked(false);
            }

            linear.addView(view1);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }
}
