package com.rootekstudio.repeatsandroid.activities;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import com.rootekstudio.repeatsandroid.JsonFile;
import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.RepeatsHelper;
import com.rootekstudio.repeatsandroid.database.DatabaseHelper;
import com.rootekstudio.repeatsandroid.notifications.AdvancedTimeNotification;
import com.rootekstudio.repeatsandroid.notifications.NotificationHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class AddAdvancedTimeActivity extends AppCompatActivity {

    GridLayout daysGrid;
    LinearLayout hoursLinear;
    LinearLayout setsLinear;
    EditText editFreq;

    int daysChecked;
    int setsChecked;
    String isEdit;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean isDark = RepeatsHelper.DarkTheme(this, false);
        setContentView(R.layout.activity_add_advanced_time);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        daysChecked = 0;
        setsChecked = 0;

        daysGrid = findViewById(R.id.daysAdvancedGrid);
        hoursLinear = findViewById(R.id.hoursLinear);
        setsLinear = findViewById(R.id.setsAdvancedLinear);
        editFreq = findViewById(R.id.editFreqAdvanced);

        editFreq.setInputType(InputType.TYPE_CLASS_NUMBER);
        editFreq.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});

        context = this;

        Intent thisIntent = getIntent();
        isEdit = thisIntent.getStringExtra("edit");

        Button saveButton = findViewById(R.id.saveDeliveryButton);
        saveButton.setOnClickListener(saveTime);

        Button cancelButton = findViewById(R.id.cancelDeliveryButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isEdit.equals("")) {
                    JSONObject rootObject = null;
                    try {
                        rootObject = new JSONObject(JsonFile.readJson(context, "advancedDelivery.json"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (rootObject.length() != 1) {
                        rootObject.remove(isEdit);
                        JsonFile.createNewJson(context, rootObject.toString(), "advancedDelivery.json");
                        NotificationHelper.cancelAdvancedAlarm(context, Integer.parseInt(isEdit));
                    }
                }
                onBackPressed();
            }
        });

        Button addHours = findViewById(R.id.addHoursButton);
        addHours.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addHoursButtons("", "08:00", "22:00");
            }
        });


        if (isEdit.equals("")) {
            loadDefaults();
        } else {
            loadSaved(isEdit);
        }

        if (!isDark) {
            View lineA = findViewById(R.id.lineA);
            lineA.setBackgroundColor(Color.parseColor("#bfbfbf"));
        }
    }

    void loadSaved(String index) {
        try {
            JSONObject rootObject = new JSONObject(JsonFile.readJson(context, "advancedDelivery.json"));
            JSONObject JSONitem = rootObject.getJSONObject(index);

            //load days
            JSONArray days = JSONitem.getJSONArray("days");
            int daysCount = days.length();

            for (int i = 0; i < daysCount; i++) {
                String day = days.getString(i);
                for (int j = i; j < daysGrid.getChildCount(); j++) {
                    CheckBox checkBox = (CheckBox) daysGrid.getChildAt(j);
                    if (checkBox.getTag().toString().equals(day)) {
                        checkBox.setChecked(true);
                        daysChecked++;
                        break;
                    }
                }
            }

            for (int i = 0; i < 7; i++) {
                CheckBox checkBox = (CheckBox) daysGrid.getChildAt(i);
                checkBox.setOnCheckedChangeListener(daysCheckedChangeListener);
            }

            //load hours
            JSONObject hours = JSONitem.getJSONObject("hours");
            int hoursCount = hours.length();

            for (int i = 0; i < hoursCount; i++) {
                JSONObject singleHour = hours.getJSONObject(String.valueOf(i));

                String from = singleHour.getString("from");
                String to = singleHour.getString("to");

                addHoursButtons(String.valueOf(i), from, to);
            }

            String frequency = JSONitem.getString("frequency");
            editFreq.setText(frequency);

            JSONArray sets = JSONitem.getJSONArray("sets");

            loadSetsList(false);

            for (int i = 0; i < sets.length(); i++) {
                String setID = sets.getString(i);

                for (int j = 0; j < setsLinear.getChildCount(); j++) {
                    CheckBox checkBox = setsLinear.getChildAt(j).findViewById(R.id.checkBoxAdvanced);
                    if (checkBox.getTag().toString().equals(setID)) {
                        checkBox.setOnCheckedChangeListener(null);
                        checkBox.setChecked(true);
                        checkBox.setOnCheckedChangeListener(setsCheckedChangeListener);
                        setsChecked++;
                        break;
                    } else {
                        checkBox.setOnCheckedChangeListener(setsCheckedChangeListener);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void loadDefaults() {
        for (int i = 0; i < daysGrid.getChildCount(); i++) {
            CheckBox checkBox = (CheckBox) daysGrid.getChildAt(i);
            checkBox.setChecked(true);
            checkBox.setOnCheckedChangeListener(daysCheckedChangeListener);
            daysChecked++;
        }
        addHoursButtons("0", "08:00", "22:00");

        editFreq.setText("30");

        loadSetsList(true);
    }

    void loadSetsList(boolean defaultCheck) {
        DatabaseHelper DB = new DatabaseHelper(this);
        ArrayList<String> titles = DB.getSingleColumn("title");
        ArrayList<String> TableNames = DB.getSingleColumn("TableName");
        for (int i = 0; i < TableNames.size(); i++) {

            final View singleCheckSet = LayoutInflater.from(context).inflate(R.layout.checkbox_layout, setsLinear, false);
            CheckBox checkBox = singleCheckSet.findViewById(R.id.checkBoxAdvanced);

            checkBox.setText(titles.get(i));
            checkBox.setTag(TableNames.get(i));
            if (defaultCheck) {
                checkBox.setChecked(true);
                checkBox.setOnCheckedChangeListener(setsCheckedChangeListener);
                setsChecked++;
            } else {
                checkBox.setOnCheckedChangeListener(setsCheckedChangeListener);
            }

            setsLinear.addView(singleCheckSet);
        }
    }

    CompoundButton.OnCheckedChangeListener daysCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            if (b) {
                daysChecked++;
            } else {
                if (daysChecked == 1) {
                    compoundButton.setChecked(true);
                    return;
                }
                daysChecked--;
            }
        }
    };

    CompoundButton.OnCheckedChangeListener setsCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if (b) {
                setsChecked++;
            } else {
                if (setsChecked == 1) {
                    compoundButton.setChecked(true);
                    return;
                }
                setsChecked--;

            }


        }
    };


    void addHoursButtons(String tag, String fromTime, String toTime) {
        final View singleHourView = LayoutInflater.from(context).inflate(R.layout.hours_layout, hoursLinear, false);
        Button from = singleHourView.findViewById(R.id.editFrom);
        Button to = singleHourView.findViewById(R.id.editTo);
        ImageButton deleteHour = singleHourView.findViewById(R.id.deleteHour);

        from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePicker((Button) view);
            }
        });

        to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePicker((Button) view);
            }
        });

        deleteHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hoursLinear.getChildCount() != 1) {
                    hoursLinear.removeView((View) view.getParent());
                }
            }
        });

        from.setText(fromTime);
        to.setText(toTime);

        hoursLinear.addView(singleHourView);
    }

    void timePicker(final Button button) {
        TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                String stringHour;
                String stringMinute;
                if (hour <= 9) {
                    stringHour = "0" + hour;
                } else {
                    stringHour = String.valueOf(hour);
                }

                if (minute <= 9) {
                    stringMinute = "0" + minute;
                } else {
                    stringMinute = String.valueOf(minute);
                }

                String time = stringHour + ":" + stringMinute;

                button.setText(time);
            }
        };

        String buttonText = (String) button.getText();
        int oldHour = Integer.parseInt(buttonText.substring(0, 2));
        int oldMinute = Integer.parseInt(buttonText.substring(3, 5));

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, timeSetListener, oldHour, oldMinute, true);
        timePickerDialog.show();
    }


    View.OnClickListener saveTime = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            View parentView = findViewById(R.id.mainSettingsLayoutAdvanced);

            int hoursSize = hoursLinear.getChildCount();
            int setsSize = setsLinear.getChildCount();

            List<String> days = new ArrayList<>();

            for (int i = 0; i < 7; i++) {
                CheckBox dayCheck = (CheckBox) daysGrid.getChildAt(i);
                if (dayCheck.isChecked()) {
                    days.add(dayCheck.getTag().toString());
                }
            }

            HashMap<String, String[]> hoursHash = new HashMap<>();

            for (int i = 0; i < hoursSize; i++) {
                RelativeLayout RL = hoursLinear.getChildAt(i).findViewById(R.id.relativeHours);
                Button fromButton = RL.findViewById(R.id.editFrom);
                Button toButton = RL.findViewById(R.id.editTo);
                String[] fromAndTo = {fromButton.getText().toString(), toButton.getText().toString()};

                hoursHash.put(String.valueOf(i), fromAndTo);
            }

            EditText editFreq = parentView.findViewById(R.id.editFreqAdvanced);
            String freq;
            if (editFreq.getText().length() == 0) {
                freq = "30";
            } else {
                freq = editFreq.getText().toString();
            }

            List<String> setNames = new ArrayList<>();

            for (int i = 0; i < setsSize; i++) {
                CheckBox setCheck = setsLinear.getChildAt(i).findViewById(R.id.checkBoxAdvanced);
                if (setCheck.isChecked()) {
                    setNames.add(setCheck.getTag().toString());
                }
            }

            try {
                JsonFile.readJson(context, "advancedDelivery.json");
                JSONObject rootObject = new JSONObject(JsonFile.readJson(context, "advancedDelivery.json"));
                Iterator<String> keys = rootObject.keys();

                int lastKey = 0;
                while (keys.hasNext()) {
                    lastKey = Integer.parseInt(keys.next());
                }
                lastKey++;

                JSONObject values = new JSONObject();

                //days
                JSONArray daysArray = new JSONArray();
                for (int i = 0; i < days.size(); i++) {
                    daysArray.put(days.get(i));
                }
                values.put("days", daysArray);

                //hours
                JSONObject hours = new JSONObject();

                for (int i = 0; i < hoursHash.size(); i++) {
                    String[] fromTO = hoursHash.get(String.valueOf(i));

                    JSONObject singleHour = new JSONObject();

                    singleHour.put("from", fromTO[0]);
                    singleHour.put("to", fromTO[1]);

                    hours.put(String.valueOf(i), singleHour);
                }

                values.put("hours", hours);

                //frequency
                values.put("frequency", freq);

                //sets
                JSONArray sets = new JSONArray();

                for (int i = 0; i < setNames.size(); i++) {
                    sets.put(setNames.get(i));
                }
                values.put("sets", sets);

                //put everything to one json
                rootObject.put(String.valueOf(lastKey), values);

                if (isEdit.equals("")) {
                    JsonFile.createNewJson(context, rootObject.toString(), "advancedDelivery.json");
                } else {
                    NotificationHelper.cancelAdvancedAlarm(context, Integer.parseInt(isEdit));
                    rootObject.remove(isEdit);
                    JsonFile.createNewJson(context, rootObject.toString(), "advancedDelivery.json");
                }

                Intent newIntent = new Intent(context, AdvancedTimeNotification.class);
                newIntent.putExtra("jsonIndex", String.valueOf(lastKey));

                NotificationHelper.registerAdvancedAlarm(context, Integer.parseInt(freq), newIntent, null, String.valueOf(lastKey));

                onBackPressed();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }
}
