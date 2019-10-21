package com.rootekstudio.repeatsandroid.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.rootekstudio.repeatsandroid.AdvancedTimeItem;
import com.rootekstudio.repeatsandroid.JsonFile;
import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.TimeAdapter;
import com.rootekstudio.repeatsandroid.database.DatabaseHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ChangeDeliveryListActivity extends AppCompatActivity {
    List<AdvancedTimeItem> timeItems = new ArrayList<>();
    Paint p = new Paint();
    RecyclerView.Adapter adapter = null;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_delivery_list);

        final Context context = this;

        recyclerView = findViewById(R.id.time_recycler_view);

        Button button = findViewById(R.id.addAdvancedDeliveryButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AddAdvancedTimeActivity.class);
                intent.putExtra("edit", "");
                startActivity(intent);
            }
        });

        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                String index = viewHolder.itemView.findViewById(R.id.cardViewAdvanced).getTag().toString();

                if(direction == ItemTouchHelper.LEFT) {
                    try {
                        JSONObject rootObject = new JSONObject(JsonFile.readJson(context, "advancedDelivery.json"));
                        rootObject.remove(index);
                        JsonFile.createNewJson(context, rootObject.toString(), "advancedDelivery.json");
                    }catch (Exception e) {
                        e.printStackTrace();
                    }

                    adapter.notifyItemRemoved(position);
                    timeItems.remove(position);
                }
                else {
                    Intent intent = new Intent(context, AddAdvancedTimeActivity.class);


                    intent.putExtra("edit", index);
                    startActivity(intent);

                    adapter.notifyItemChanged(position);
                }

            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

                View itemView = viewHolder.itemView;

                if (dX > 0) {
                    p.setColor(Color.parseColor("#247EE6"));
                    c.drawRect((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom(), p);
                } else {
                    p.setColor(Color.RED);
                    c.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(), itemView.getRight(), (float) itemView.getBottom(), p);
                }
            }

        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }


    @Override
    protected void onStart(){
        super.onStart();
        timeItems = new ArrayList<>();

        loadFromJSON();

        adapter = new TimeAdapter(timeItems);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    void loadFromJSON() {
        try {
            JSONObject rootObject = new JSONObject(JsonFile.readJson(this, "advancedDelivery.json"));
            Iterator<String> iterator = rootObject.keys();

            DatabaseHelper DB = new DatabaseHelper(this);

            int allItemsCount = 1;

            while (iterator.hasNext()) {
                String index = iterator.next();

                AdvancedTimeItem timeItem = new AdvancedTimeItem();
                timeItem.setName(String.valueOf(allItemsCount));
                timeItem.setId(index);

                JSONObject object = rootObject.getJSONObject(index);

                //read days
                JSONArray days = object.getJSONArray("days");
                StringBuilder daysBuilder = new StringBuilder();
                daysBuilder.append(getString(R.string.days));
                daysBuilder.append(" ");

                int daysCount = days.length();
                for (int i = 0; i < daysCount; i++) {
                    String day = days.getString(i);

                    switch(day){
                        case "1":
                            daysBuilder.append(getString(R.string.sun));
                            break;
                        case "2":
                            daysBuilder.append(getString(R.string.mon));
                            break;
                        case "3":
                            daysBuilder.append(getString(R.string.tue));
                            break;
                        case "4":
                            daysBuilder.append(getString(R.string.wed));
                            break;
                        case "5":
                            daysBuilder.append(getString(R.string.thu));
                            break;
                        case "6":
                            daysBuilder.append(getString(R.string.fri));
                            break;
                        case "7":
                            daysBuilder.append(getString(R.string.sat));
                            break;
                    }
                    if (i != daysCount - 1) {
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

                int hoursLength = hoursObject.length();
                int count = 0;

                while (hoursKeys.hasNext()) {
                    String hourID = hoursKeys.next();
                    JSONObject single = hoursObject.getJSONObject(hourID);
                    String from = single.getString("from");
                    String to = single.getString("to");

                    hoursBuilder.append(from);
                    hoursBuilder.append(" - ");
                    hoursBuilder.append(to);

                    if(count != hoursLength-1) {
                        hoursBuilder.append(", ");
                    }

                    count++;
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

                for (int i = 0; i < setsArrayLength; i++) {
                    setsNames.append(DB.getValue("title", "TitleTable", "TableName='" + setsArray.getString(i) + "'"));

                    if (i != setsArrayLength - 1) {
                        setsNames.append(", ");
                    }
                }

                timeItem.setSets(setsNames.toString());
                timeItems.add(timeItem);

                allItemsCount++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
