package com.rootekstudio.repeatsandroid.reminders;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarView;
import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.database.RepeatsDatabase;

import java.util.Calendar;

public class EditReminder {
    PopupWindow popupWindow;
    boolean enabled;

    public EditReminder(View view, String setID) {
        editReminder(view, setID);
    }

    void editReminder(View view, String setID) {
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;

        View popupView = LayoutInflater.from(view.getContext()).inflate(R.layout.set_reminder_layout, null);
        Switch reminderSwitch = popupView.findViewById(R.id.remindersSwitch);
        reminderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                enabled = isChecked;
            }
        });

        RelativeLayout relativeWithSwitch = popupView.findViewById(R.id.relativeSwitchReminder);
        relativeWithSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reminderSwitch.setChecked(!reminderSwitch.isChecked());
            }
        });

        EditText daysEditText = popupView.findViewById(R.id.editTextDaysReminder);

        Button cancelButton = popupView.findViewById(R.id.cancelPopupReminder);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        Button saveButton = popupView.findViewById(R.id.saveReminder);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalendarView calendarView = popupView.findViewById(R.id.calendarViewReminders);
                Calendar selectedDate = calendarView.getFirstSelectedDate();

                String days = daysEditText.getText().toString();

                long todayInMillis = Calendar.getInstance().getTimeInMillis();
                long selectedDateMillis = selectedDate.getTimeInMillis();

                if(days.equals("")) {
                    Toast.makeText(view.getContext(), "Wpisz liczbę dni", Toast.LENGTH_LONG).show();
                    return;
                }

                if (selectedDateMillis < todayInMillis) {
                    Toast.makeText(view.getContext(), "Nie możesz ustawić przypomnienia w przeszłości xD", Toast.LENGTH_LONG).show();
                    return;
                }

                int daysInt = Integer.parseInt(days);

                long daysInMillis = daysInt * 1000 * 60 * 60 * 24;
                long reminderDateInMillis = selectedDate.getTimeInMillis();

                if (reminderDateInMillis - daysInMillis < todayInMillis) {
                    Toast.makeText(view.getContext(), "Moje zaawansowane algorytmy AI i Machine Learning obliczyły, że dzień który wybrałeś minus ilość dni przed tym terminem daje wynik" +
                            " ujemny, co oznacza, że chcesz żeby przypomnienie przyszło w przeszłości xD zmień ilość dni i będzie git", Toast.LENGTH_LONG).show();
                    return;
                }


                int day = selectedDate.get(Calendar.DAY_OF_MONTH);
                int month = selectedDate.get(Calendar.MONTH);
                int year = selectedDate.get(Calendar.YEAR);

                String dayString = String.valueOf(day);
                String monthString = String.valueOf(month);
                String yearString = String.valueOf(year);

                if (day < 10) {
                    dayString = "0" + day;
                }

                if (month < 10) {
                    monthString = "0" + month;
                }

                String deadlineDate = yearString + "-" + monthString + "-" + dayString;
                RepeatsDatabase.getInstance(view.getContext()).updateReminderCalendar(setID, deadlineDate, days, enabled);
            }
        });

        popupWindow = new PopupWindow(popupView, width, height, true);
        popupWindow.setAnimationStyle(R.style.animation);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
    }
}
