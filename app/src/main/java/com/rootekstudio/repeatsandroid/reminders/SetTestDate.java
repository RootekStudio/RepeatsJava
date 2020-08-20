package com.rootekstudio.repeatsandroid.reminders;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.database.RepeatsDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

public class SetTestDate {
    PopupWindow popupWindow;
    public SetTestDate(View view, String setID, boolean isSettings) throws OutOfDateRangeException, ParseException {
        ReminderInfo reminderInfo = RepeatsDatabase.getInstance(view.getContext()).getInfoAboutReminderFromCalendar(setID);
        View popupView = LayoutInflater.from(view.getContext()).inflate(R.layout.calendar_picker, null);
        CalendarView calendarView = popupView.findViewById(R.id.calendarViewReminders);
        Button cancel  = popupView.findViewById(R.id.cancelPopupTestDate);
        Button save = popupView.findViewById(R.id.saveTestDate);

        if(reminderInfo.getDeadline() != null) {
            Calendar deadlineCalendar = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            deadlineCalendar.setTime(Objects.requireNonNull(simpleDateFormat.parse(reminderInfo.getDeadline())));
            calendarView.setDate(deadlineCalendar);
        }

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar selectedDate = calendarView.getFirstSelectedDate();

                if (selectedDate.getTimeInMillis() < Calendar.getInstance().getTimeInMillis()) {
                    Toast.makeText(view.getContext(), v.getContext().getString(R.string.invalid_date), Toast.LENGTH_LONG).show();
                    return;
                }

                int day = selectedDate.get(Calendar.DAY_OF_MONTH);
                int month = selectedDate.get(Calendar.MONTH) + 1;
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
                RepeatsDatabase.getInstance(view.getContext()).updateTestDate(setID, deadlineDate);

                View parent = (ViewGroup)view.getParent();
                TextView textView = parent.findViewById(R.id.testDate);
                textView.setText(view.getContext().getString(R.string.test_date, DateFormat.getDateInstance().format(selectedDate.getTime())));

                if(reminderInfo.getDeadline() != null) {
                    if(selectedDate.getTimeInMillis() - reminderInfo.getReminderDaysBefore() * 1000 * 60 * 60 * 24 < Calendar.getInstance().getTimeInMillis()) {
                        Toast.makeText(v.getContext(), v.getContext().getString(R.string.date_not_compatible), Toast.LENGTH_LONG).show();
                        if(isSettings) {
                            View viewParent = (View)view.getParent();
                            Switch switchReminder = viewParent.findViewById(R.id.reminderSwitchSettings);
                            switchReminder.setChecked(false);
                        }
                        else {
                            TextView reminderStatus = parent.getRootView().findViewById(R.id.reminderStatus);
                            reminderStatus.setText(view.getContext().getResources().getString(R.string.reminder_not_set));
                            reminderStatus.setTextColor(view.getContext().getResources().getColor(R.color.redRepeats));
                        }

                        RepeatsDatabase.getInstance(view.getContext()).updateReminderEnabled(setID, false);
                    }
                }
                popupWindow.dismiss();
            }
        });

        popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
        popupWindow.setAnimationStyle(R.style.animation);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
    }
}
