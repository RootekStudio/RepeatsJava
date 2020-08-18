package com.rootekstudio.repeatsandroid.reminders;

public class ReminderDayAndName implements Comparable<ReminderDayAndName> {
    private int daysBefore;
    private String setName;

    public ReminderDayAndName(int daysBefore, String setID) {
        this.daysBefore = daysBefore;
        this.setName = setID;
    }

    public int getDaysBefore() {
        return daysBefore;
    }

    public void setDaysBefore(int daysBefore) {
        this.daysBefore = daysBefore;
    }

    public String getSetName() {
        return setName;
    }

    public void setSetName(String setName) {
        this.setName = setName;
    }

    @Override
    public int compareTo(ReminderDayAndName o) {
        return this.daysBefore - o.getDaysBefore();
    }
}
