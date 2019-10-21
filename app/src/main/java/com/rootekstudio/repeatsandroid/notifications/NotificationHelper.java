package com.rootekstudio.repeatsandroid.notifications;

import android.content.Context;

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

    static boolean checkHours(int fromHour, int toHour, int fromMinute, int toMinute ) {

        Calendar calendar = GregorianCalendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        if (fromHour > toHour) {
            //hour in range - let's check minutes
            if(!(hour < fromHour && hour > toHour)) {
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
                }
                else {
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
                }
                else {
                    return false;
                }
            }
        }
        else if(fromMinute != toMinute){
            //minute in range
            if(minute >= fromMinute && minute < toMinute){
                return false;
            }
            else if(minute >= fromMinute && fromMinute > toMinute) {
                return false;
            }
        }

        return true;
    }

    static void stopAndRegisterInFuture(String day, int hour, int minute, Context context, int id) {
        Calendar calendarAlarm = Calendar.getInstance();

        if(day.equals("today")) {
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

            if(calendarCheck.before(calendarNow) || calendarCheck.equals(calendarNow)) {
                calendarAlarm.add(Calendar.DATE, 1);
            }
        }
        else {
            calendarAlarm.setTimeInMillis(System.currentTimeMillis());
            calendarAlarm.add(Calendar.DAY_OF_MONTH, )
        }


        NotifiSetup.CancelNotifications(context);
        NotifiSetup.RegisterNotifications(context, calendarAlarm, id);
    }
}
