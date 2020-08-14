package com.rootekstudio.repeatsandroid.settings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.database.RepeatsDatabase;
import com.rootekstudio.repeatsandroid.database.Values;
import com.rootekstudio.repeatsandroid.reminders.EditReminder;
import com.rootekstudio.repeatsandroid.reminders.ReminderInfo;
import com.rootekstudio.repeatsandroid.reminders.SetReminders;
import com.rootekstudio.repeatsandroid.reminders.SetTestDate;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class ManageRemindersActivity extends AppCompatActivity {

    LinearLayout mainLinearLayout;
    List<ReminderInfo> remindersInfos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_reminders);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mainLinearLayout = findViewById(R.id.linearManageReminders);
        remindersInfos = RepeatsDatabase.getInstance(this).getInfoAboutAllReminders(Values.ORDER_BY_ID_DESC);

        for(int i = 0; i < remindersInfos.size(); i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.single_reminder_settings_card, null);

            FrameLayout frame = view.findViewById(R.id.manageRemindersFrame);
            TextView setName = view.findViewById(R.id.setNameReminderSettings);
            Switch reminderSwitch = view.findViewById(R.id.reminderSwitchSettings);
            RelativeLayout relativeTestDate = view.findViewById(R.id.relativeTestDateSettings);
            TextView testDate = view.findViewById(R.id.testDate);
            RelativeLayout relativeReminderDate = view.findViewById(R.id.relativeReminderDateSettings);
            TextView reminderDate = view.findViewById(R.id.reminderDateReminderSettings);
            RelativeLayout relativeSwitch = view.findViewById(R.id.relativeSwitchReminderSettings);

            relativeSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reminderSwitch.setChecked(!reminderSwitch.isChecked());
                }
            });

            frame.setTag(remindersInfos.get(i).getSetID());

            setName.setText(RepeatsDatabase.getInstance(this).setNameResolver(remindersInfos.get(i).getSetID()));
            if(remindersInfos.get(i).getEnabled() == 1) {
                reminderSwitch.setChecked(true);
            } else {
                reminderSwitch.setChecked(false);
            }

            if(remindersInfos.get(i).getDeadline() != null) {
                Calendar deadlineCalendar = Calendar.getInstance();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    deadlineCalendar.setTime(Objects.requireNonNull(simpleDateFormat.parse(remindersInfos.get(i).getDeadline())));
                    testDate.setText(getString(R.string.test_date, DateFormat.getDateInstance().format(deadlineCalendar.getTime())));

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                testDate.setText(getString(R.string.test_date, getString(R.string.none)));
            }

            reminderDate.setText(getResources().getQuantityString(R.plurals.reminder_days, remindersInfos.get(i).getReminderDaysBefore(), remindersInfos.get(i).getReminderDaysBefore()));

            reminderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    View viewParent = (View)buttonView.getParent().getParent().getParent();
                    String setID = (String)viewParent.getTag();
                    if(isChecked) {
                        if(canReminder(setID)) {
                            RepeatsDatabase.getInstance(buttonView.getContext())
                                    .updateReminderEnabled(setID, true);
                            SetReminders.restartReminders(buttonView.getContext());
                        } else {
                            reminderSwitch.setOnCheckedChangeListener(null);

                            reminderSwitch.setChecked(false);
                            Toast.makeText(buttonView.getContext(), getString(R.string.change_date_to_reminder), Toast.LENGTH_LONG).show();

                            reminderSwitch.setOnCheckedChangeListener(this);
                        }

                    } else {
                        RepeatsDatabase.getInstance(buttonView.getContext())
                                .updateReminderEnabled(setID, false);
                        SetReminders.restartReminders(buttonView.getContext());
                    }
                }
            });

            relativeTestDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View viewParent = (View)v.getParent().getParent();
                    String setID = (String)viewParent.getTag();
                    try {
                        new SetTestDate(v, setID, true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            relativeReminderDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View viewParent = (View)v.getParent().getParent();
                    String setID = (String)viewParent.getTag();
                    new EditReminder(v, setID, true);
                }
            });

            mainLinearLayout.addView(view);
        }
    }

    private boolean canReminder(String setID) {
        ReminderInfo reminderInfo = RepeatsDatabase.getInstance(this).getInfoAboutReminderFromCalendar(setID);

        if(reminderInfo.getDeadline() != null) {
            Calendar calendar = Calendar.getInstance();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                calendar.setTime(Objects.requireNonNull(simpleDateFormat.parse(reminderInfo.getDeadline())));
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            long daysBeforeInMillis = reminderInfo.getReminderDaysBefore() * 1000 * 60 * 60 * 24;

            return calendar.getTimeInMillis() - daysBeforeInMillis > Calendar.getInstance().getTimeInMillis();
        }
        else {
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }
}