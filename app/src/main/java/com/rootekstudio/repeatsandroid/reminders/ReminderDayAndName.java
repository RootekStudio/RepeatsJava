package com.rootekstudio.repeatsandroid.reminders;

public class ReminderDayAndName implements Comparable<ReminderDayAndName> {
    private int daysBefore;
    private String setID;

    public ReminderDayAndName(int daysBefore, String setID) {
        this.daysBefore = daysBefore;
        this.setID = setID;
    }

    public int getDaysBefore() {
        return daysBefore;
    }

    public void setDaysBefore(int daysBefore) {
        this.daysBefore = daysBefore;
    }

    public String getSetID() {
        return setID;
    }

    public void setSetID(String setID) {
        this.setID = setID;
    }

    @Override
    public int compareTo(ReminderDayAndName o) {
        return this.daysBefore - o.getDaysBefore();
    }
}
