package com.rootekstudio.repeatsandroid.notifications;

public class NotificationInfo {
    private String setID;
    private String daysOfWeek;
    private String hours;
    private int mode;

    public NotificationInfo() {

    }

    public String getSetID() {
        return setID;
    }

    public void setSetID(String setID) {
        this.setID = setID;
    }

    public String getDaysOfWeek() {
        return daysOfWeek;
    }

    public void setDaysOfWeek(String daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }
}