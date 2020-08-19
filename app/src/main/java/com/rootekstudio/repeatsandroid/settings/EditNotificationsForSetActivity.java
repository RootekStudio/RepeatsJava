package com.rootekstudio.repeatsandroid.settings;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.RepeatsHelper;
import com.rootekstudio.repeatsandroid.database.RepeatsDatabase;
import com.rootekstudio.repeatsandroid.notifications.NotificationHelper;
import com.rootekstudio.repeatsandroid.notifications.NotificationInfo;
import com.rootekstudio.repeatsandroid.notifications.NotificationsScheduler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class EditNotificationsForSetActivity extends AppCompatActivity {
    LinearLayout hoursLinear;
    boolean is24HourFormat;
    int mode;

    Chip mondayChip;
    Chip tuesdayChip;
    Chip wednesdayChip;
    Chip thursdayChip;
    Chip fridayChip;
    Chip saturdayChip;
    Chip sundayChip;

    String setID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        is24HourFormat = android.text.format.DateFormat.is24HourFormat(this);

        setContentView(R.layout.activity_edit_notifications_for_set);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        RelativeLayout relativeSwitch = findViewById(R.id.relativeSwitchNotifications);
        Switch notificationsSwitch = findViewById(R.id.notificationsSwitch);
        mondayChip = findViewById(R.id.mondayChip);
        tuesdayChip = findViewById(R.id.tuesdayChip);
        wednesdayChip = findViewById(R.id.wednesdayChip);
        thursdayChip = findViewById(R.id.thursdayChip);
        fridayChip = findViewById(R.id.fridayChip);
        saturdayChip = findViewById(R.id.saturdayChip);
        sundayChip = findViewById(R.id.sundayChip);
        Button addHorus = findViewById(R.id.addHoursButton);
        hoursLinear = findViewById(R.id.hoursLinearNotificationsSettings);

        List<String> hoursList = new ArrayList<>();

        setID = getIntent().getStringExtra("setID");

        if(getIntent().getBooleanExtra("fromSettings", false)) {
            relativeSwitch.setVisibility(View.GONE);
        }

        NotificationInfo notificationInfo = RepeatsDatabase.getInstance(this).singleSetNotificationInfo(setID);
        String daysOfWeek = notificationInfo.getDaysOfWeek();
        String hours = notificationInfo.getHours();

        String defaultFrom = "08:00";
        String defaultTo = "20:00";

        relativeSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notificationsSwitch.setChecked(!notificationsSwitch.isChecked());
            }
        });

        if (notificationInfo.getMode() == 0) {
            mode = 0;
            notificationsSwitch.setChecked(false);
        } else if (notificationInfo.getMode() == 1) {
            mode = 1;
            notificationsSwitch.setChecked(true);
        }

        if(getIntent().getBooleanExtra("requestedTurnOn", false)) {
            mode = 1;
        }

        notificationsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mode = 1;
                } else {
                    mode = 0;
                }
            }
        });

        addHorus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addHourLayout(defaultFrom, defaultTo);
            }
        });

        if (daysOfWeek == null) {
            mondayChip.setChecked(true);
            tuesdayChip.setChecked(true);
            wednesdayChip.setChecked(true);
            thursdayChip.setChecked(true);
            fridayChip.setChecked(true);
            saturdayChip.setChecked(true);
            sundayChip.setChecked(true);

            addHourLayout(defaultFrom, defaultTo);

        } else {
            if (daysOfWeek.contains("1")) {
                sundayChip.setChecked(true);
            }

            if (daysOfWeek.contains("2")) {
                mondayChip.setChecked(true);
            }

            if (daysOfWeek.contains("3")) {
                tuesdayChip.setChecked(true);
            }

            if (daysOfWeek.contains("4")) {
                wednesdayChip.setChecked(true);
            }

            if (daysOfWeek.contains("5")) {
                thursdayChip.setChecked(true);
            }

            if (daysOfWeek.contains("6")) {
                fridayChip.setChecked(true);
            }

            if (daysOfWeek.contains("7")) {
                saturdayChip.setChecked(true);
            }

            Scanner scannerHours = new Scanner(hours);
            while (scannerHours.hasNextLine()) {
                hoursList.add(scannerHours.nextLine());
            }

            for(int i = 0; i < hoursList.size(); i++) {
                String from = hoursList.get(i).substring(0,5);
                String to = hoursList.get(i).substring(6,11);

                addHourLayout(from, to);
            }
        }
    }

    void addHourLayout(String from, String to) {
        View viewHours = LayoutInflater.from(this).inflate(R.layout.hours_layout, hoursLinear, false);
        Button editFromButton = viewHours.findViewById(R.id.editFrom);
        Button editToButton = viewHours.findViewById(R.id.editTo);
        ImageButton deleteButton = viewHours.findViewById(R.id.deleteHour);

        if (is24HourFormat) {
            editFromButton.setText(from);
            editToButton.setText(to);
        } else {
            editFromButton.setText(h24Toh12Converter(from));
            editToButton.setText(h24Toh12Converter(to));
        }

        editFromButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePicker((Button) v);
            }
        });

        editToButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePicker((Button) v);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hoursLinear.getChildCount() != 1) {
                    hoursLinear.removeView((View) v.getParent());
                }
            }
        });

        hoursLinear.addView(viewHours);
    }

    public void cancelClick(View view) {
        onBackPressed();
    }

    public void saveClick(View view) {
        NotificationInfo saveNewNotificationInfo = new NotificationInfo();
        saveNewNotificationInfo.setMode(mode);
        String days = "";

        if (sundayChip.isChecked()) {
            days += "1" + RepeatsHelper.breakLine;
        }
        if (mondayChip.isChecked()) {
            days += "2" + RepeatsHelper.breakLine;
        }
        if (tuesdayChip.isChecked()) {
            days += "3" + RepeatsHelper.breakLine;
        }
        if (wednesdayChip.isChecked()) {
            days += "4" + RepeatsHelper.breakLine;
        }
        if (thursdayChip.isChecked()) {
            days += "5" + RepeatsHelper.breakLine;
        }
        if (fridayChip.isChecked()) {
            days += "6" + RepeatsHelper.breakLine;
        }
        if (saturdayChip.isChecked()) {
            days += "7" + RepeatsHelper.breakLine;
        }

        if(days.equals("")) {
            Toast.makeText(this, getString(R.string.select_at_least_one_day), Toast.LENGTH_LONG).show();
            return;
        }

        saveNewNotificationInfo.setDaysOfWeek(days);

        StringBuilder hours = new StringBuilder();

        for (int i = 0; i < hoursLinear.getChildCount(); i++) {
            RelativeLayout rl = (RelativeLayout) hoursLinear.getChildAt(i);
            Button fromButton = rl.findViewById(R.id.editFrom);
            Button toButton = rl.findViewById(R.id.editTo);

            String fromTime;
            String toTime;
            if(!is24HourFormat) {
                fromTime = h12Toh24Converter(fromButton.getText().toString());
                toTime = h12Toh24Converter(toButton.getText().toString());
            }
            else {
                fromTime = fromButton.getText().toString();
                toTime = toButton.getText().toString();
            }

            hours.append(fromTime).append("-").append(toTime).append(RepeatsHelper.breakLine);
        }

        saveNewNotificationInfo.setHours(hours.toString());
        saveNewNotificationInfo.setSetID(setID);

        RepeatsDatabase.getInstance(this).updateSingleSetNotificationInfo(saveNewNotificationInfo);
        NotificationsScheduler.restartNotifications(this);
        onBackPressed();
    }

    String h24Toh12Converter(String time) {
        SimpleDateFormat h12Format = new SimpleDateFormat("hh:mm a");
        SimpleDateFormat h24Format = new SimpleDateFormat("HH:mm");

        Date date = null;
        try {
            date = h24Format.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return h12Format.format(date);
    }

    String h12Toh24Converter(String time) {
        SimpleDateFormat h12Format = new SimpleDateFormat("hh:mm a");
        SimpleDateFormat h24Format = new SimpleDateFormat("HH:mm");

        Date date = null;
        try {
            date = h12Format.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return h24Format.format(date);
    }

    void timePicker(Button button) {
        TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hour, int minute) {
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

                View parent = (View)button.getParent();

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                long fromMillis = 0;
                long toMillis = 0;

                if(button == parent.findViewById(R.id.editFrom)) {
                    calendar.set(Calendar.HOUR_OF_DAY, hour);
                    calendar.set(Calendar.MINUTE, minute);
                    fromMillis = calendar.getTimeInMillis();

                    Button toButton = parent.findViewById(R.id.editTo);
                    String toTime = toButton.getText().toString();

                    calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(toTime.substring(0,2)));
                    calendar.set(Calendar.MINUTE, Integer.parseInt(toTime.substring(3,5)));

                    toMillis = calendar.getTimeInMillis();
                } else if(button == parent.findViewById(R.id.editTo)) {
                    calendar.set(Calendar.HOUR_OF_DAY, hour);
                    calendar.set(Calendar.MINUTE, minute);
                    toMillis = calendar.getTimeInMillis();

                    Button toButton = parent.findViewById(R.id.editFrom);
                    String toTime = toButton.getText().toString();

                    calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(toTime.substring(0,2)));
                    calendar.set(Calendar.MINUTE, Integer.parseInt(toTime.substring(3,5)));

                    fromMillis = calendar.getTimeInMillis();
                }

                if(fromMillis > toMillis) {
                    Toast.makeText(button.getContext(), getString(R.string.from_not_greater_than_to), Toast.LENGTH_LONG).show();
                    return;
                } else if(fromMillis == toMillis) {
                    Toast.makeText(button.getContext(), getString(R.string.from_to_cannot_be_same), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (is24HourFormat) {
                    button.setText(time);
                } else {
                    button.setText(h24Toh12Converter(time));
                }
            }
        };

        int oldHour;
        int oldMinute;

        if (is24HourFormat) {
            oldHour = Integer.parseInt(button.getText().toString().substring(0, 2));
        } else {
            oldHour = Integer.parseInt(h12Toh24Converter(button.getText().toString()).substring(0, 2));
        }
        oldMinute = Integer.parseInt(button.getText().toString().substring(3, 5));

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, timeSetListener, oldHour,
                oldMinute, is24HourFormat);
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