package com.rootekstudio.repeatsandroid.settings;

import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.RepeatsHelper;
import com.rootekstudio.repeatsandroid.UIHelper;
import com.rootekstudio.repeatsandroid.notifications.ConstNotifiSetup;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.Iterator;

public class SilenceHoursActivity extends AppCompatActivity {

    File jsonFile;
    LinearLayout rootLayout;
    Context context;
    int lastIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean isDark = UIHelper.DarkTheme(this, false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_silence_hours);

        context = this;
        jsonFile = new File(getFilesDir(), "silenceHours.json");

        rootLayout = findViewById(R.id.rootLayoutSilence);

        Button addHoursButton = findViewById(R.id.addHoursButton);
        addHoursButton.setOnClickListener(addHours);

        String from = "";
        String to = "";

        try {
            JSONObject rootObject = getJSON();
            Iterator<String> iterator = rootObject.keys();

            while (iterator.hasNext()) {
                String index = iterator.next();

                View view = LayoutInflater.from(this).inflate(R.layout.hours_layout, rootLayout, false);

                JSONObject object = rootObject.getJSONObject(index);
                from = object.getString("from");
                to = object.getString("to");

                addButtons(view, index, from, to);

                rootLayout.addView(view);

                lastIndex = Integer.parseInt(index);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    JSONObject getJSON() {
        JSONObject rootObject = null;
        try {
            FileInputStream jsonStream = new FileInputStream(jsonFile);
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

    View.OnClickListener addHours = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            View hoursLayout = LayoutInflater.from(context).inflate(R.layout.hours_layout, rootLayout, false);

            lastIndex++;

            try {
                JSONObject rootObject = getJSON();
                JSONObject newObject = new JSONObject();
                newObject.put("from", "22:00");
                newObject.put("to", "06:00");

                rootObject.put(String.valueOf(lastIndex), newObject);

                FileWriter fileWriter = new FileWriter(jsonFile);
                String jsonSTRING = rootObject.toString();
                fileWriter.write(jsonSTRING);
                fileWriter.flush();
                fileWriter.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
            ConstNotifiSetup.CancelNotifications(context);
            ConstNotifiSetup.RegisterNotifications(context, null, RepeatsHelper.staticFrequencyCode);

            addButtons(hoursLayout, String.valueOf(lastIndex), "22:00", "06:00");
            rootLayout.addView(hoursLayout);
        }
    };

    void addButtons(View view, String tag, String from, String to) {
        Button buttonFrom = view.findViewById(R.id.editFrom);
        Button buttonTo = view.findViewById(R.id.editTo);
        ImageButton deleteHour = view.findViewById(R.id.deleteHour);

        buttonFrom.setText(from);
        buttonTo.setText(to);

        buttonFrom.setTag(tag);
        buttonTo.setTag(tag);
        deleteHour.setTag(tag);

        buttonFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePicker((Button) view);
            }
        });

        buttonTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePicker((Button) view);
            }
        });

        deleteHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rootLayout.getChildCount() != 1) {
                    JSONObject rootObject = getJSON();
                    rootObject.remove(String.valueOf(view.getTag()));

                    try {
                        FileWriter fileWriter = new FileWriter(jsonFile);
                        String jsonSTRING = rootObject.toString();
                        fileWriter.write(jsonSTRING);
                        fileWriter.flush();
                        fileWriter.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    ConstNotifiSetup.CancelNotifications(context);
                    ConstNotifiSetup.RegisterNotifications(context, null, RepeatsHelper.staticFrequencyCode);

                    rootLayout.removeView((View) view.getParent());
                }
            }
        });
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

                try {
                    JSONObject rootObject = getJSON();
                    String index = button.getTag().toString();
                    JSONObject object = rootObject.getJSONObject(index);

                    if (button.getId() == R.id.editFrom) {
                        object.remove("from");
                        object.put("from", time);
                    } else if (button.getId() == R.id.editTo) {
                        object.remove("to");
                        object.put("to", time);
                    }

                    FileWriter fileWriter = new FileWriter(jsonFile);
                    String jsonSTRING = rootObject.toString();
                    fileWriter.write(jsonSTRING);
                    fileWriter.flush();
                    fileWriter.close();

                    ConstNotifiSetup.CancelNotifications(context);
                    ConstNotifiSetup.RegisterNotifications(context, null, RepeatsHelper.staticFrequencyCode);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                button.setText(time);
            }
        };
        String buttonText = (String) button.getText();
        int oldHour = Integer.parseInt(buttonText.substring(0, 2));
        int oldMinute = Integer.parseInt(buttonText.substring(3, 5));

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, timeSetListener, oldHour, oldMinute, true);
        timePickerDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }
}
