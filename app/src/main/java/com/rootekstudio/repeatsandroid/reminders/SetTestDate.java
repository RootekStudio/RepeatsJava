package com.rootekstudio.repeatsandroid.reminders;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.database.RepeatsDatabase;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Objects;

public class SetTestDate {
    PopupWindow popupWindow;
    public SetTestDate(View view, String setID) throws OutOfDateRangeException, ParseException {
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
                    Toast.makeText(view.getContext(), "Nie możesz ustawić przypomnienia w przeszłości xD", Toast.LENGTH_LONG).show();
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

                DateFormat dateFormat = DateFormat.getDateInstance();

                View parent = (ViewGroup)view.getParent();
                TextView textView = parent.findViewById(R.id.testDate);
                textView.setText(dateFormat.format(selectedDate.getTime()));

                popupWindow.dismiss();
            }
        });

        popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
        popupWindow.setAnimationStyle(R.style.animation);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
    }
}
