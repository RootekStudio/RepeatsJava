package com.rootekstudio.repeatsandroid.notifications;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class NotificationHelper {
    static String checkDays(ArrayList<String> days) {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeekNow = calendar.get(Calendar.DAY_OF_WEEK);

        int nearestDay = 8;
        int nearestDiff = 8;

        for(String s : days) {
            int day = Integer.parseInt(s);
            if(day > dayOfWeekNow) {
                int difference = day - dayOfWeekNow;
                if(difference < nearestDiff){
                    nearestDiff = difference;
                    nearestDay = day;
                }
            } else if(day == dayOfWeekNow) {
                return String.valueOf(day);
            }
        }

        if(nearestDay == 8) {
            for(String s : days) {
                int day = Integer.parseInt(s);
                if(day < dayOfWeekNow) {
                    if(day < nearestDay) {
                        nearestDay = day;
                    }
                }
            }
        }

        return String.valueOf(nearestDay);
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
}
