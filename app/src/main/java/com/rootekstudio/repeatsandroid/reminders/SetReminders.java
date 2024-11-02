package com.rootekstudio.repeatsandroid.reminders;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.database.RepeatsDatabase;
import com.rootekstudio.repeatsandroid.settings.SharedPreferencesManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.rootekstudio.repeatsandroid.RequestCodes.REMINDER_ALARM_ID;

public class SetReminders {
    public static void startReminders(Context context) {
        RepeatsDatabase DB = RepeatsDatabase.getInstance(context);

        List<ReminderInfo> reminders = DB.listOfEnabledReminders();
        int earliestDay = 367;
        List<ReminderDayAndName> setsNamesAndDaysBefore = new ArrayList<>();
        StringBuilder setsIDsBuilder = new StringBuilder();
        StringBuilder daysBeforeBuilder = new StringBuilder();

        if(reminders.size() != 0) {
            for(int i = 0; i < reminders.size(); i++) {
                String deadline = reminders.get(i).getDeadline();
                int daysBefore = reminders.get(i).getReminderDaysBefore();

                Calendar calendar = Calendar.getInstance();
                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    calendar.setTime(Objects.requireNonNull(simpleDateFormat.parse(deadline)));
                } catch (Exception e) {
                    Toast.makeText(context, context.getString(R.string.error_setting_reminders), Toast.LENGTH_SHORT).show();
                    return;
                }

                calendar.add(Calendar.DAY_OF_YEAR, -daysBefore);

                int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
                if(dayOfYear < earliestDay) {
                    setsNamesAndDaysBefore.clear();
                    setsNamesAndDaysBefore.add(new ReminderDayAndName(reminders.get(i).getReminderDaysBefore(), reminders.get(i).getSetID()));

                    earliestDay = dayOfYear;
                }
                else if(dayOfYear == earliestDay) {
                    setsNamesAndDaysBefore.add(new ReminderDayAndName(reminders.get(i).getReminderDaysBefore(), reminders.get(i).getSetID()));
                }
            }

            Collections.sort(setsNamesAndDaysBefore);

            for(int i = 0; i < setsNamesAndDaysBefore.size(); i++) {
                setsIDsBuilder.append("\n").append(setsNamesAndDaysBefore.get(i).getSetID());
                daysBeforeBuilder.append("\n").append(setsNamesAndDaysBefore.get(i).getDaysBefore());
            }

            String setsIDs = setsIDsBuilder.toString().replaceFirst("\n", "");
            String days = daysBeforeBuilder.toString().replaceFirst("\n", "");

            Calendar reminderCalendar = Calendar.getInstance();
            reminderCalendar.set(Calendar.SECOND, 0);
            reminderCalendar.set(Calendar.MILLISECOND, 0);
            reminderCalendar.set(Calendar.DAY_OF_YEAR, earliestDay);

            SharedPreferencesManager sharedPreferencesManager = SharedPreferencesManager.getInstance(context);

            int hour = Integer.parseInt(sharedPreferencesManager.getRemindersTime().substring(0,2));
            int minute = Integer.parseInt(sharedPreferencesManager.getRemindersTime().substring(3,5));

            reminderCalendar.set(Calendar.HOUR_OF_DAY, hour);
            reminderCalendar.set(Calendar.MINUTE, minute);

            long millis = reminderCalendar.getTimeInMillis();

            Intent intent = new Intent(context, ReminderBroadcastReceiver.class);
            intent.putExtra("setsIDs", setsIDs);
            intent.putExtra("daysBefore", days);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, REMINDER_ALARM_ID, intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        if(alarmManager.canScheduleExactAlarms()) {
                            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                                    millis,
                                    pendingIntent);
                        } else {
                            Toast.makeText(context, context.getString(R.string.error_allow_exact_alarms), Toast.LENGTH_SHORT).show();
                            //TO-DO: request user to allow exact alarms
                        }
                    } else {
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                                millis,
                                pendingIntent);
                    }


                } else {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP,
                            millis,
                            pendingIntent);
                }

        }
    }

    public static void stopReminders(Context context) {
        Intent intent = new Intent(context, ReminderBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, REMINDER_ALARM_ID, intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    public static void restartReminders(Context context) {
        stopReminders(context);
        startReminders(context);
    }

    public static boolean checkIfReminderIsRegistered(Context context) {
        return (PendingIntent.getBroadcast(context, REMINDER_ALARM_ID, new Intent(context, ReminderBroadcastReceiver.class), PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE) != null);
    }
}
