package com.rootekstudio.repeatsandroid.notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class NotificationHelper {
    static String checkDays(ArrayList<String> days) {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        boolean allLower = false;
        boolean today = false;
        for (String s : days) {
            int day = Integer.parseInt(s);
            if (dayOfWeek == day) {
                today = true;
                break;
            } else allLower = day < dayOfWeek;
        }

        if (today) {
            return "today";
        } else if (allLower) {
            return days.get(0);
        } else {
            int index = 0;
            int min = 7;
            for (int i = 0; i < days.size(); i++) {
                int day = Integer.parseInt(days.get(i));
                int math;
                if (day < dayOfWeek) {
                    math = dayOfWeek - day;
                } else {
                    math = day - dayOfWeek;
                }

                if (math < min) {
                    min = math;
                    index = i;
                }
            }

            return days.get(index);
        }
    }

    static boolean checkHours(int fromHour, int toHour, int fromMinute, int toMinute) {

        Calendar calendar = GregorianCalendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        if (fromHour > toHour) {
            //hour in range - let's check minutes
            if (!(hour < fromHour && hour > toHour)) {
                //Check if I'm near the set hour
                if (hour == fromHour) {
                    if (minute >= fromMinute) {
                        //stop notifications and register new
                        return false;
                    }
                }
                //Check again if I'm near the set hour
                else if (hour == toHour) {
                    if (minute < toMinute) {
                        //stop notifications and register new
                        return false;
                    }
                } else {
                    return false;
                }
            }
        } else if (fromHour < toHour) {
            //hour in range - stop notifications
            if (hour >= fromHour && hour <= toHour) {
                //Check if I'm near the set hour
                if (hour == fromHour) {
                    if (minute >= fromMinute) {
                        //stop notifications and register new
                        return false;
                    }
                }
                //Check again if I'm near the set hour
                else if (hour == toHour) {
                    if (minute < toMinute) {
                        //stop notifications and register new
                        return false;
                    }
                } else {
                    return false;
                }
            }
        } else if (fromMinute != toMinute) {
            //minute in range
            if (minute >= fromMinute && minute < toMinute) {
                return false;
            } else if (minute >= fromMinute && fromMinute > toMinute) {
                return false;
            }
        }

        return true;
    }

    static void stopAndRegisterInFuture(String day, int hour, int minute, Context context, int id) {
        Calendar calendarAlarm = Calendar.getInstance();

        if (day.equals("today")) {
            Calendar calendarCheck = Calendar.getInstance();
            Calendar calendarNow = Calendar.getInstance();
            calendarCheck.setTimeInMillis(System.currentTimeMillis());
            calendarNow.setTimeInMillis(System.currentTimeMillis());

            calendarCheck.set(Calendar.HOUR_OF_DAY, hour);
            calendarCheck.set(Calendar.MINUTE, minute);
            calendarCheck.set(Calendar.SECOND, 0);

            calendarAlarm.clear();
            calendarAlarm.set(Calendar.HOUR_OF_DAY, hour);
            calendarAlarm.set(Calendar.MINUTE, minute);
            calendarAlarm.set(Calendar.SECOND, 0);
        } else {
            calendarAlarm.setTimeInMillis(System.currentTimeMillis());
            int setsDayOfWeek = Integer.valueOf(day);
            int realDayOfWeek = calendarAlarm.get(Calendar.DAY_OF_WEEK);

            if (setsDayOfWeek < realDayOfWeek) {
                int math = realDayOfWeek - setsDayOfWeek;
                int toAdd = 7 - math;
                calendarAlarm.add(Calendar.DAY_OF_MONTH, toAdd);
            } else if(setsDayOfWeek == realDayOfWeek) {
                calendarAlarm.add(Calendar.DAY_OF_MONTH, 7);
            }
            else{
                int toAdd = setsDayOfWeek - realDayOfWeek;
                calendarAlarm.add(Calendar.DAY_OF_MONTH, toAdd);
            }

            calendarAlarm.set(Calendar.HOUR_OF_DAY, hour);
            calendarAlarm.set(Calendar.MINUTE, minute);
            calendarAlarm.set(Calendar.SECOND, 0);
        }

        Intent newIntent = new Intent(context, AdvancedTimeNotification.class);
        newIntent.putExtra("jsonIndex", String.valueOf(id));
        NotificationHelper.registerAdvancedAlarm(context, 0, newIntent, calendarAlarm, String.valueOf(id));
    }

    public static void registerAdvancedAlarm(Context context, int time, Intent intent, Calendar calendar, String index) {
        long triggerAtMillis;

        if (calendar == null) {
            triggerAtMillis = System.currentTimeMillis() + 1000 * 60 * time;
        } else {
            triggerAtMillis = calendar.getTimeInMillis();
        }

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, Integer.parseInt(index), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent);
        }
    }
    public static void cancelAdvancedAlarm(Context cnt, int code) {
        Intent intent = new Intent(cnt, AdvancedTimeNotification.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(cnt, code, intent, 0);
        AlarmManager alarmManager = (AlarmManager) cnt.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }
}
