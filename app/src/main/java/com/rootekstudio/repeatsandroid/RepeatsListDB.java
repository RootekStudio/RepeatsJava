package com.rootekstudio.repeatsandroid;

public class RepeatsListDB {
    public String Title;
    public String TableName;
    public String CreateDate;
    public String IsEnabled;
    public String Avatar;
    public String IgnoreChars;
    public String firstLanguage;
    public String secondLanguage;

    public RepeatsListDB() {
    }

    public RepeatsListDB(String Title, String TableName, String CreateDate, String IsEnabled, String Avatar, String IgnoreChars, String firstLanguage, String secondLanguage) {
        this.Title = Title;
        this.TableName = TableName;
        this.CreateDate = CreateDate;
        this.IsEnabled = IsEnabled;
        this.Avatar = Avatar;
        this.IgnoreChars = IgnoreChars;
        this.firstLanguage = firstLanguage;
        this.secondLanguage = secondLanguage;
    }

    public String getitle() {
        return Title;
    }

    public String getTableName() {
        return TableName;
    }

    public String getCreateDate() {
        return CreateDate;
    }

    public String getIsEnabled() {
        return IsEnabled;
    }

    public String getAvatar() {
        return Avatar;
    }

    public String getIgnoreChars() {
        return IgnoreChars;
    }

    public String getFirstLanguage() {return firstLanguage;}

    public String getSecondLanguage() {return secondLanguage;}

    public void setTitle(String Title) {
        this.Title = Title;
    }

    public void setTableName(String TableName) {
        this.TableName = TableName;
    }

    public void setCreateDate(String CreateDate) {
        this.CreateDate = CreateDate;
    }

    public void setIsEnabled(String IsEnabled) {
        this.IsEnabled = IsEnabled;
    }

    public void setAvatar(String Avatar) {
        this.Avatar = Avatar;
    }

    public void setIgnoreChars(String IgnoreChars) {
        this.IgnoreChars = IgnoreChars;
    }

    public void setFirstLanguage(String firstLanguage) {this.firstLanguage = firstLanguage;}

    public void setSecondLanguage(String secondLanguage) {this.secondLanguage = secondLanguage;}
}
