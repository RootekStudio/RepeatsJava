package com.rootekstudio.repeatsandroid.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.rootekstudio.repeatsandroid.AdvancedTimeItem;
import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.TimeAdapter;
import com.rootekstudio.repeatsandroid.database.DatabaseHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ChangeDeliveryListActivity extends AppCompatActivity {
    List<AdvancedTimeItem> timeItems = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_delivery_list);

        final Context context = this;

        RecyclerView recyclerView = findViewById(R.id.time_recycler_view);
        Button button = findViewById(R.id.addAdvancedDeliveryButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AddAdvancedTimeActivity.class);
                startActivity(intent);
            }
        });

        loadFromJSON();

        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        RecyclerView.Adapter adapter = new TimeAdapter(timeItems);
        recyclerView.setAdapter(adapter);
    }

    void loadFromJSON() {
        try {
            JSONObject rootObject = getJSON();
            Iterator<String> iterator = rootObject.keys();

            DatabaseHelper DB = new DatabaseHelper(this);

            while (iterator.hasNext()) {
                AdvancedTimeItem timeItem = new AdvancedTimeItem();
                timeItem.setName("testName");

                String index = iterator.next();

                JSONObject object = rootObject.getJSONObject(index);

                //read days
                JSONArray days = object.getJSONArray("days");
                StringBuilder daysBuilder = new StringBuilder();
                daysBuilder.append(getString(R.string.days));
                daysBuilder.append(" ");

                int daysCount = days.length();
                for(int i = 0; i < daysCount; i++) {
                    daysBuilder.append(days.getString(i));

                    if(i != daysCount-1){
                        daysBuilder.append(", ");
                    }
                }
                timeItem.setDays(daysBuilder.toString());

                //read hours

                JSONObject hoursObject = object.getJSONObject("hours");
                Iterator<String> hoursKeys = hoursObject.keys();
                StringBuilder hoursBuilder = new StringBuilder();
                hoursBuilder.append(getString(R.string.hours));
                hoursBuilder.append(" ");

                while(hoursKeys.hasNext()) {
                    String hourID = hoursKeys.next();
                    JSONObject single = hoursObject.getJSONObject(hourID);
                    String from = single.getString("from");
                    String to = single.getString("to");

                    hoursBuilder.append(from);
                    hoursBuilder.append(" - ");
                    hoursBuilder.append(to);
                }

                timeItem.setHours(hoursBuilder.toString());

                //read frequency
                String frequency = getString(R.string.frequency) + " " + object.getString("frequency") + " " + getString(R.string.minutes);
                timeItem.setFrequency(frequency);

                JSONArray setsArray = object.getJSONArray("sets");
                StringBuilder setsNames = new StringBuilder();
                setsNames.append(getString(R.string.sets));
                setsNames.append(" ");

                int setsArrayLength = setsArray.length();

                for(int i = 0; i < setsArrayLength; i++) {
                    setsNames.append(DB.getValue("title","TitleTable", "TableName='" + setsArray.getString(i) + "'"));

                    if(i != setsArrayLength-1) {
                        setsNames.append(", ");
                    }
                }

                timeItem.setSets(setsNames.toString());
                timeItems.add(timeItem);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    JSONObject getJSON() {
        File jsonAdvanced = new File(getFilesDir(), "advancedDelivery.json");
        JSONObject rootObject = null;
        try {
            FileInputStream jsonStream = new FileInputStream(jsonAdvanced);
            BufferedReader jReader = new BufferedReader(new InputStreamReader(jsonStream));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = jReader.readLine()) != null) {
                sb.append(line);
            }

            String fullJSON = sb.toString();

            rootObject = new JSONObject(fullJSON);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return rootObject;
    }

}
