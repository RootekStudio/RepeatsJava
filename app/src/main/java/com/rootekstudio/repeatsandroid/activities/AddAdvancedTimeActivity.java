package com.rootekstudio.repeatsandroid.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TimePicker;

import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.database.DatabaseHelper;
import com.rootekstudio.repeatsandroid.notifications.NotifiSetup;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class AddAdvancedTimeActivity extends AppCompatActivity {

    LinearLayout daysLinear;
    LinearLayout hoursLinear;
    LinearLayout setsLinear;

    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_advanced_time);

        daysLinear = findViewById(R.id.daysAdvancedLinear);
        hoursLinear = findViewById(R.id.hoursLinear);
        setsLinear = findViewById(R.id.setsAdvancedLinear);

        context = this;

        Button saveButton = findViewById(R.id.saveDeliveryButton);
        saveButton.setOnClickListener(saveTime);

        Button addHours = findViewById(R.id.addHoursButton);
        addHours.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addHoursButtons("", "08:00", "22:00");
            }
        });

        loadDefaults();
    }

    void loadDefaults() {
        RelativeLayout RL = hoursLinear.findViewById(R.id.relativeHours);
        Button from = RL.findViewById(R.id.editFrom);
        Button to = RL.findViewById(R.id.editTo);
        from.setText("08:00");
        to.setText("22:00");

        from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePicker((Button)view);
            }
        });

        to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePicker((Button)view);
            }
        });

        EditText editFreq = findViewById(R.id.editFreqAdvanced);
        editFreq.setText("30");

        LinearLayout setLinear = findViewById(R.id.setsAdvancedLinear);

        DatabaseHelper DB = new DatabaseHelper(this);
        ArrayList<String> titles = DB.getSingleColumn("title");
        ArrayList<String> TableNames = DB.getSingleColumn("TableName");

        for(int i = 0; i < TableNames.size(); i++) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(titles.get(i));
            checkBox.setTag(TableNames.get(i));
            checkBox.setChecked(true);

            setLinear.addView(checkBox);
        }
    }

    void addHoursButtons(String tag, String fromTime, String toTime) {
        View hoursLayout = LayoutInflater.from(context).inflate(R.layout.hours_layout, hoursLinear, false);
        Button from = hoursLayout.findViewById(R.id.editFrom);
        Button to = hoursLayout.findViewById(R.id.editTo);

        from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePicker((Button)view);
            }
        });

        to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePicker((Button)view);
            }
        });

        from.setText(fromTime);
        to.setText(toTime);

        hoursLinear.addView(hoursLayout);

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
            View parentView = (View)view.getParent();

            int hoursSize = hoursLinear.getChildCount();
            int setsSize = setsLinear.getChildCount();

            List<String> days = new ArrayList<>();

            for(int i  = 0; i < 7; i++) {
                CheckBox dayCheck = (CheckBox)daysLinear.getChildAt(i);
                if(dayCheck.isChecked()) {
                    days.add(dayCheck.getTag().toString());
                }
            }

            HashMap<String, String[]> hoursHash = new HashMap<>();

            for(int i = 0; i < hoursSize; i++) {
                RelativeLayout RL = hoursLinear.getChildAt(i).findViewById(R.id.relativeHours);
                Button fromButton = RL.findViewById(R.id.editFrom);
                Button toButton = RL.findViewById(R.id.editTo);
                String[] fromAndTo = {fromButton.getText().toString(), toButton.getText().toString()};

                hoursHash.put(String.valueOf(i), fromAndTo);
            }

            EditText editFreq = parentView.findViewById(R.id.editFreqAdvanced);
            String freq = editFreq.getText().toString();

            List<String> setNames = new ArrayList<>();

            for(int i = 0; i < setsSize; i++) {
                CheckBox setCheck = (CheckBox)setsLinear.getChildAt(i);
                if(setCheck.isChecked()) {
                    setNames.add(setCheck.getTag().toString());
                }
            }

            File jsonAdvanced = new File(getFilesDir(), "advancedDelivery.json");
                try {
                    JSONObject rootObject = getJSON();
                    Iterator<String> keys = rootObject.keys();

                    int lastKey = 0;
                    while(keys.hasNext()) {
                        lastKey = Integer.parseInt(keys.next());
                    }
                    lastKey++;

                    JSONObject values = new JSONObject();

                    //days
                    JSONArray daysArray = new JSONArray();
                        for(int i = 0; i < days.size(); i++) {
                            daysArray.put(days.get(i));
                        }
                    values.put("days", daysArray);

                    //hours
                    JSONObject hours = new JSONObject();
                    JSONObject singleHour = new JSONObject();

                    for(int i = 0; i < hoursHash.size(); i++) {
                        String[] fromTO = hoursHash.get(String.valueOf(i));

                        singleHour.put("from", fromTO[0]);
                        singleHour.put("to", fromTO[1]);

                        hours.put(String.valueOf(i), singleHour);
                    }

                    values.put("hours", hours);

                    //frequency
                    values.put("frequency", freq);

                    //sets
                    JSONArray sets = new JSONArray();

                    for(int i = 0; i < setNames.size(); i++) {
                        sets.put(setNames.get(i));
                    }
                    values.put("sets", sets);

                    //put everything to one json
                    rootObject.put(String.valueOf(lastKey), values);

                    FileWriter fileWriter = new FileWriter(jsonAdvanced);
                    String jsonSTRING = rootObject.toString();
                    fileWriter.write(jsonSTRING);
                    fileWriter.flush();
                    fileWriter.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
    };

    JSONObject getJSON() {
        JSONObject rootObject = null;
        try {
            File jsonAdvanced = new File(getFilesDir(), "advancedDelivery.json");

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
