package com.rootekstudio.repeatsandroid.database;

public class SingleSetInfo {
    private String setID;
    private String setName;
    private String CreateDate;
    private int IsEnabled;
    private int IgnoreChars;
    private String firstLanguage;
    private String secondLanguage;
    private int goodAnswers;
    private int wrongAnswers;

    public SingleSetInfo() {
    }

    public SingleSetInfo(String setID, String setName, String CreateDate, int IsEnabled, int IgnoreChars, String firstLanguage, String secondLanguage) {
        this.setID = setID;
        this.setName = setName;
        this.CreateDate = CreateDate;
        this.IsEnabled = IsEnabled;
        this.IgnoreChars = IgnoreChars;
        this.firstLanguage = firstLanguage;
        this.secondLanguage = secondLanguage;
        this.goodAnswers = 0;
        this.wrongAnswers = 0;
    }

    public String getSetID() {
        return setID;
    }

    public String getSetName() {
        return setName;
    }

    public String getCreateDate() {
        return CreateDate;
    }

    public int getIsEnabled() {
        return IsEnabled;
    }

    public int getIgnoreChars() {
        return IgnoreChars;
    }

    public String getFirstLanguage() {
        return firstLanguage;
    }

    public String getSecondLanguage() {
        return secondLanguage;
    }

    public int getGoodAnswers() {
        return goodAnswers;
    }

    public int getWrongAnswers() {
        return wrongAnswers;
    }

    public void setSetID(String Title) {
        this.setID = Title;
    }

    public void setSetName(String TableName) {
        this.setName = TableName;
    }

    public void setCreateDate(String CreateDate) {
        this.CreateDate = CreateDate;
    }

    public void setIsEnabled(int IsEnabled) { this.IsEnabled = IsEnabled; }

    public void setIgnoreChars(int IgnoreChars) {
        this.IgnoreChars = IgnoreChars;
    }

    public void setFirstLanguage(String firstLanguage) {
        this.firstLanguage = firstLanguage;
    }

    public void setSecondLanguage(String secondLanguage) {
        this.secondLanguage = secondLanguage;
    }

    public void setGoodAnswers(int goodAnswers) {
        this.goodAnswers = goodAnswers;
    }

    public void setWrongAnswers(int wrongAnswers) {
        this.wrongAnswers = wrongAnswers;
    }

}