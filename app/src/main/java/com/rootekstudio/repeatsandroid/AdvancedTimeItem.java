package com.rootekstudio.repeatsandroid;

public class AdvancedTimeItem {
    public String id;
    public String name;
    private String days;
    private String hours;
    public String frequency;
    public String sets;

    public AdvancedTimeItem() {

    }

    public AdvancedTimeItem(String id, String name, String days, String hours, String frequency, String sets) {
        this.id = id;
        this.name = name;
        this.days = days;
        this.hours = hours;
        this.frequency = frequency;
        this.sets = sets;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDays() {
        return days;
    }

    public String getHours() {
        return hours;
    }

    public String getFrequency() {
        return frequency;
    }

    public String getSets() {
        return sets;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public void setSets(String sets) {
        this.sets = sets;
    }


}
