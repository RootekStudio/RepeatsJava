package com.rootekstudio.repeatsandroid.notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.rootekstudio.repeatsandroid.RepeatsHelper;
import com.rootekstudio.repeatsandroid.RequestCodes;
import com.rootekstudio.repeatsandroid.database.RepeatsDatabase;
import com.rootekstudio.repeatsandroid.settings.SharedPreferencesManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

public class NotificationsScheduler {
    public static void scheduleNotifications(Context context) {
        List<NotificationInfo> notificationInfoList = RepeatsDatabase.getInstance(context).enabledNotificationsInfo();

        Calendar now = Calendar.getInstance();
        now.set(Calendar.SECOND, 0);
        now.set(Calendar.MILLISECOND, 0);

        long timeInMillis = now.getTimeInMillis();

        Calendar scheduledCalendar = Calendar.getInstance();
        scheduledCalendar.setTimeInMillis(timeInMillis);
        scheduledCalendar.add(Calendar.MINUTE, SharedPreferencesManager.getInstance(context).getFrequency());

        int scheduledDayOfWeek = scheduledCalendar.get(Calendar.DAY_OF_WEEK);

        List<NotificationInfo> scheduleLater = new ArrayList<>();
        StringBuilder setsToScheduleNow = new StringBuilder();

        if(notificationInfoList.size() != 0) {
            for (int i = 0; i < notificationInfoList.size(); i++) {
                NotificationInfo notificationInfo = notificationInfoList.get(i);
                if (notificationInfo.getDaysOfWeek().contains(String.valueOf(scheduledDayOfWeek))) {
                    List<String> allHours = new ArrayList<>();

                    Scanner hoursScanner = new Scanner(notificationInfo.getHours());
                    while (hoursScanner.hasNextLine()) {
                        allHours.add(hoursScanner.nextLine());
                    }

                    for (int j = 0; j < allHours.size(); j++) {
                        String hours = allHours.get(j);

                        String fromTime = hours.substring(0, 5);
                        String toTime = hours.substring(6, 11);

                        Calendar fromCalendar = Calendar.getInstance();
                        Calendar toCalendar = Calendar.getInstance();
                        fromCalendar.setTimeInMillis(timeInMillis);
                        toCalendar.setTimeInMillis(timeInMillis);

                        fromCalendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(fromTime.substring(0, 2)));
                        fromCalendar.set(Calendar.MINUTE, Integer.parseInt(fromTime.substring(3, 5)));

                        toCalendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(toTime.substring(0, 2)));
                        toCalendar.set(Calendar.MINUTE, Integer.parseInt(toTime.substring(3, 5)));

                        if (scheduledCalendar.getTimeInMillis() >= fromCalendar.getTimeInMillis()
                                && scheduledCalendar.getTimeInMillis() < toCalendar.getTimeInMillis()) {

                            setsToScheduleNow.append(notificationInfo.getSetID()).append(RepeatsHelper.breakLine);
                            break;
                        }
                    }

                    if (!setsToScheduleNow.toString().contains(notificationInfo.getSetID())) {
                        scheduleLater.add(notificationInfo);
                    }

                } else {
                    scheduleLater.add(notificationInfo);
                }
            }

            String setsIDToScheduleNow = setsToScheduleNow.toString();
            if (setsIDToScheduleNow.equals("")) {
                String setsIDToSchedule = "";

                Calendar calendarToSchedule = Calendar.getInstance();
                calendarToSchedule.setTimeInMillis(timeInMillis);

                calendarToSchedule.add(Calendar.DAY_OF_YEAR, 8);

                for (int i = 0; i < scheduleLater.size(); i++) {
                    NotificationInfo notificationInfo = scheduleLater.get(i);
                    ArrayList<String> days = new ArrayList<>();
                    Scanner scanner = new Scanner(notificationInfo.getDaysOfWeek());
                    while (scanner.hasNextLine()) {
                        days.add(scanner.nextLine());
                    }

                    List<String> hours = new ArrayList<>();
                    Scanner scannerHours = new Scanner(notificationInfo.getHours());
                    while (scannerHours.hasNextLine()) {
                        hours.add(scannerHours.nextLine());
                    }

                    int nearestDay = Integer.parseInt(NotificationHelper.checkDays(days));

                    Calendar compareCalendar = Calendar.getInstance();
                    compareCalendar.setTimeInMillis(timeInMillis);

                    boolean canTodayDay = false;
                    if(nearestDay == now.get(Calendar.DAY_OF_WEEK)) {
                        for (int j = 0; j < hours.size(); j++) {
                            String fromHour = hours.get(j).substring(0,2);
                            String fromMinute = hours.get(j).substring(3,5);

                            compareCalendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(fromHour));
                            compareCalendar.set(Calendar.MINUTE, Integer.parseInt(fromMinute));

                            if(now.getTimeInMillis() <= compareCalendar.getTimeInMillis()) {
                                canTodayDay = true;
                                if(compareCalendar.getTimeInMillis() < calendarToSchedule.getTimeInMillis()) {
                                    calendarToSchedule.setTimeInMillis(compareCalendar.getTimeInMillis());
                                    setsIDToSchedule = notificationInfo.getSetID() + RepeatsHelper.breakLine;
                                } else if(compareCalendar.getTimeInMillis() == calendarToSchedule.getTimeInMillis()) {
                                    setsIDToSchedule += notificationInfo.getSetID() + RepeatsHelper.breakLine;
                                }
                            }
                        }
                        if(!canTodayDay) {
                            days.remove(String.valueOf(now.get(Calendar.DAY_OF_WEEK)));
                        }
                    }

                    if(!canTodayDay) {
                        if(days.size() != 0) {
                            nearestDay = Integer.parseInt(NotificationHelper.checkDays(days));

                            if (nearestDay < now.get(Calendar.DAY_OF_WEEK)) {
                                compareCalendar.add(Calendar.DAY_OF_YEAR, (7-now.get(Calendar.DAY_OF_WEEK)) + nearestDay);
                            }
                            else if (nearestDay > now.get(Calendar.DAY_OF_WEEK)) {
                                compareCalendar.add(Calendar.DAY_OF_YEAR, (nearestDay - now.get(Calendar.DAY_OF_WEEK)));
                            }

                            //if not today
                            for(int j = 0; j < hours.size(); j++) {
                                String fromHour = hours.get(j).substring(0,2);
                                String fromMinute = hours.get(j).substring(3,5);

                                compareCalendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(fromHour));
                                compareCalendar.set(Calendar.MINUTE, Integer.parseInt(fromMinute));

                                if(compareCalendar.getTimeInMillis() < calendarToSchedule.getTimeInMillis()) {
                                    calendarToSchedule.setTimeInMillis(compareCalendar.getTimeInMillis());
                                    setsIDToSchedule = notificationInfo.getSetID() + RepeatsHelper.breakLine;
                                } else if(compareCalendar.getTimeInMillis() == calendarToSchedule.getTimeInMillis()) {
                                    setsIDToSchedule += notificationInfo.getSetID() + RepeatsHelper.breakLine;
                                }
                            }
                        }
                    }
                }

                if(!setsIDToSchedule.equals("")) {
                    scheduleAlarm(context, setsIDToSchedule, calendarToSchedule.getTimeInMillis());
                }
            } else {
                scheduleAlarm(context, setsIDToScheduleNow, scheduledCalendar.getTimeInMillis());
            }
        }
    }

    private static void scheduleAlarm(Context context, String setsIDs, long millis) {
        Intent intent = new Intent(context, NotificationBroadcastReceiver.class);
        intent.putExtra("setsIDs", setsIDs);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, RequestCodes.PENDING_INTENT_QUESTION_NOTIFICATION_REQUEST_CODE, intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                    millis,
                    pendingIntent);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP,
                    millis,
                    pendingIntent);
        }
    }

    public static void stopNotifications(Context context) {
        Intent intent = new Intent(context, NotificationBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, RequestCodes.PENDING_INTENT_QUESTION_NOTIFICATION_REQUEST_CODE, intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    public static void restartNotifications(Context context) {
        stopNotifications(context);
        scheduleNotifications(context);
    }
}
