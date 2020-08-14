package com.rootekstudio.repeatsandroid.reminders;

import android.annotation.SuppressLint;
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
import android.widget.TextView;
import android.widget.Toast;

import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.database.RepeatsDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

public class EditReminder {
    PopupWindow popupWindow;
    boolean enabled;
    Calendar calendar;

    public EditReminder(View view, String setID, boolean isSettings) {
        editReminder(view, setID, isSettings);
    }

    void editReminder(View view, String setID, boolean isSettings) {
        calendar = Calendar.getInstance();
        ReminderInfo reminderInfo = RepeatsDatabase.getInstance(view.getContext()).getInfoAboutReminderFromCalendar(setID);

        View popupView = LayoutInflater.from(view.getContext()).inflate(R.layout.set_reminder_layout, null);
        Switch reminderSwitch = popupView.findViewById(R.id.remindersSwitch);
        EditText daysEditText = popupView.findViewById(R.id.editTextDaysReminder);
        Button cancelButton = popupView.findViewById(R.id.cancelPopupReminder);
        Button saveButton = popupView.findViewById(R.id.saveReminder);
        RelativeLayout relativeWithSwitch = popupView.findViewById(R.id.relativeSwitchReminder);

        if(isSettings) {
            relativeWithSwitch.setVisibility(View.GONE);
        }

        if(reminderInfo.getEnabled() == 1) {
            reminderSwitch.setChecked(true);
            enabled = true;
        }
        else {
            reminderSwitch.setChecked(false);
            enabled = false;
        }

        String deadlineDate = reminderInfo.getDeadline();

        if(deadlineDate != null) {
            calendar = Calendar.getInstance();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                calendar.setTime(Objects.requireNonNull(simpleDateFormat.parse(deadlineDate)));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        daysEditText.setText(String.valueOf(reminderInfo.getReminderDaysBefore()));

        reminderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                enabled = isChecked;
            }
        });


        relativeWithSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reminderSwitch.setChecked(!reminderSwitch.isChecked());
            }
        });



        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String days = daysEditText.getText().toString();

                long todayInMillis = Calendar.getInstance().getTimeInMillis();

                if(days.equals("")) {
                    Toast.makeText(view.getContext(), view.getContext().getString(R.string.enter_days), Toast.LENGTH_LONG).show();
                    return;
                }

                int daysInt = Integer.parseInt(days);

                if(daysInt == 0) {
                    Toast.makeText(view.getContext(), view.getContext().getString(R.string.enter_days), Toast.LENGTH_LONG).show();
                    return;
                }

                long daysInMillis = daysInt * 1000 * 60 * 60 * 24;
                long testDateInMillis = calendar.getTimeInMillis();

                TextView reminderStatus = view.findViewById(R.id.reminderStatus);

                if(enabled) {
                    if (testDateInMillis - daysInMillis < todayInMillis) {
                        Toast.makeText(view.getContext(), view.getContext().getString(R.string.invalid_date), Toast.LENGTH_LONG).show();
                        return;
                    }

                    if(!isSettings) {
                        RepeatsDatabase.getInstance(view.getContext()).updateReminderEnabled(setID, enabled);
                    }

                    RepeatsDatabase.getInstance(view.getContext()).updateReminderDaysBefore(setID, days);

                    if(reminderStatus != null) {
                        reminderStatus.setText(view.getContext().getResources().getString(R.string.reminder_set));
                        reminderStatus.setTextColor(view.getContext().getResources().getColor(R.color.greenRepeats));
                    }

                    SetReminders.startReminders(view.getContext());
                }
                else {
                    RepeatsDatabase.getInstance(view.getContext()).updateReminderEnabled(setID, enabled);
                    RepeatsDatabase.getInstance(view.getContext()).updateReminderDaysBefore(setID, days);

                    if(!isSettings) {
                        reminderStatus.setText(view.getContext().getResources().getString(R.string.reminder_not_set));
                        reminderStatus.setTextColor(view.getContext().getResources().getColor(R.color.redRepeats));
                    } else {
                        TextView reminderInfo = view.findViewById(R.id.reminderDateReminderSettings);
                        reminderInfo.setText(view.getContext().getResources().getQuantityString(R.plurals.reminder_days, daysInt, daysInt));
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
