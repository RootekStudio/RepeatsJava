package com.rootekstudio.repeatsandroid.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.rootekstudio.repeatsandroid.backup.SetFullInfo;
import com.rootekstudio.repeatsandroid.backup.SetItemContent;
import com.rootekstudio.repeatsandroid.fastlearning.FastLearningSetsListItem;
import com.rootekstudio.repeatsandroid.notifications.NotificationInfo;
import com.rootekstudio.repeatsandroid.reminders.ReminderInfo;
import com.rootekstudio.repeatsandroid.statistics.SetStats;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class RepeatsDatabase extends SQLiteOpenHelper {
    private static RepeatsDatabase single_instance = null;

    private RepeatsDatabase(Context context) {
        super(context, "repeats_database", null, 1);
    }

    public static synchronized RepeatsDatabase getInstance(Context context) {
        if (single_instance == null) {
            single_instance = new RepeatsDatabase(context.getApplicationContext());
        }
        return single_instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String create = "CREATE TABLE IF NOT EXISTS " + Values.sets_info + " (" +
                Values.set_id + " TEXT PRIMARY KEY NOT NULL, " +
                Values.set_name + " TEXT, " +
                Values.creation_date + " DATETIME, " +
                Values.enabled + " INTEGER DEFAULT 1, " +
                Values.ignore_chars + " INTEGER DEFAULT 0, " +
                Values.first_lang + " TEXT, " +
                Values.second_lang + " TEXT, " +
                Values.good_answers + " INTEGER DEFAULT 0, " +
                Values.wrong_answers + " INTEGER DEFAULT 0);";

        db.execSQL(create);

        String createCalendar = "CREATE TABLE IF NOT EXISTS " + Values.calendar + " (" +
                Values.set_id + " TEXT PRIMARY KEY NOT NULL, " +
                Values.deadline + " TEXT, " +
                Values.reminder_days_before + " INTEGER DEFAULT 2, " +
                Values.notifications_days_of_week + " TEXT, " +
                Values.notifications_hours + " TEXT, " +
                Values.notifications_mode + " INTEGER DEFAULT 0, " +
                Values.reminder_enabled + " INTEGER DEFAULT 0);";

        db.execSQL(createCalendar);

        String query = "INSERT INTO " + Values.calendar + " (" + Values.set_id + ") SELECT " + Values.set_id + " FROM " + Values.sets_info + ";";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void updateSingleSetNotificationInfo(NotificationInfo notificationInfo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Values.notifications_days_of_week, notificationInfo.getDaysOfWeek());
        contentValues.put(Values.notifications_hours, notificationInfo.getHours());
        contentValues.put(Values.notifications_mode, notificationInfo.getMode());

        db.update(Values.calendar, contentValues, Values.set_id + "=?", new String[]{notificationInfo.getSetID()});
        db.close();
    }

    public NotificationInfo singleSetNotificationInfo(String setID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " +
                Values.notifications_days_of_week + ", " +
                Values.notifications_hours + ", " +
                Values.notifications_mode + " FROM " +
                Values.calendar + " WHERE " +
                Values.set_id + " = '" + setID +"';";

        Cursor cursor = db.rawQuery(query, null);
        NotificationInfo notificationInfo = new NotificationInfo();

        if(cursor.moveToFirst()) {
            notificationInfo.setSetID(setID);
            notificationInfo.setDaysOfWeek(cursor.getString(0));
            notificationInfo.setHours(cursor.getString(1));
            notificationInfo.setMode(cursor.getInt(2));
        }

        cursor.close();
        db.close();
        return notificationInfo;
    }

    public List<NotificationInfo> setsNotificationsInfo() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " +
                Values.set_id + ", " +
                Values.notifications_days_of_week + ", " +
                Values.notifications_hours + ", " +
                Values.notifications_mode + " FROM " +
                Values.calendar + ";";

        Cursor cursor = db.rawQuery(query, null);

        List<NotificationInfo> notificationInfos = new ArrayList<>();
        if(cursor.moveToFirst()) {
            do {
                NotificationInfo notificationInfo = new NotificationInfo();
                notificationInfo.setSetID(cursor.getString(0));
                notificationInfo.setDaysOfWeek(cursor.getString(1));
                notificationInfo.setHours(cursor.getString(2));
                notificationInfo.setMode(cursor.getInt(3));
                notificationInfos.add(notificationInfo);
            } while(cursor.moveToNext());

        }

        cursor.close();
        db.close();
        return notificationInfos;
    }

    public List<NotificationInfo> enabledNotificationsInfo() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " +
                Values.set_id + ", " +
                Values.notifications_days_of_week + ", " +
                Values.notifications_hours + ", " +
                Values.notifications_mode + " FROM " +
                Values.calendar + " WHERE " + Values.notifications_mode + " IN (1,2);";

        Cursor cursor = db.rawQuery(query, null);

        List<NotificationInfo> notificationInfos = new ArrayList<>();
        if(cursor.moveToFirst()) {
            do {
                NotificationInfo notificationInfo = new NotificationInfo();
                notificationInfo.setSetID(cursor.getString(0));
                notificationInfo.setDaysOfWeek(cursor.getString(1));
                notificationInfo.setHours(cursor.getString(2));
                notificationInfo.setMode(cursor.getInt(3));
                notificationInfos.add(notificationInfo);
            } while(cursor.moveToNext());

        }

        cursor.close();
        db.close();
        return notificationInfos;
    }

    public boolean areNotificationsEnabledForSet(String setID) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT " + Values.notifications_mode  + " FROM " + Values.calendar + " WHERE " + Values.set_id + " = '" + setID + "';";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            if(cursor.getInt(0) == 0) {
                cursor.close();
                db.close();
                return false;
            }
            else {
                cursor.close();
                db.close();
                return true;
            }
        } else {
            cursor.close();
            db.close();
            return false;
        }
    }

    public void createCalendarForOlderVersions() {
        if (!isCalendarCreated()) {
            SQLiteDatabase db = this.getWritableDatabase();
            String createCalendar = "CREATE TABLE IF NOT EXISTS " + Values.calendar + " (" +
                    Values.set_id + " TEXT PRIMARY KEY NOT NULL, " +
                    Values.deadline + " TEXT, " +
                    Values.reminder_days_before + " INTEGER DEFAULT 2, " +
                    Values.notifications_days + " TEXT, " +
                    Values.notifications_days_of_week + " TEXT, " +
                    Values.notifications_hours + " TEXT, " +
                    Values.notifications_silent_hours + " TEXT, " +
                    Values.notifications_mode + " INTEGER DEFAULT 0, " +
                    Values.reminder_enabled + " INTEGER DEFAULT 0);";

            db.execSQL(createCalendar);

            String query = "INSERT INTO " + Values.calendar + " (" + Values.set_id + ") SELECT " + Values.set_id + " FROM " + Values.sets_info + ";";
            db.execSQL(query);
            db.close();
        }
    }

    private boolean isCalendarCreated() {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT name FROM sqlite_master WHERE type='table' AND name='calendar'";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            cursor.close();
            db.close();
            return true;
        } else {
            cursor.close();
            db.close();
            return false;
        }
    }

    public void updateReminderEnabled(String setsID, boolean isEnabled) {
        SQLiteDatabase db = this.getWritableDatabase();

        int reminderEnabled;
        if (isEnabled) {
            reminderEnabled = 1;
        } else {
            reminderEnabled = 0;
        }

        setsID = setsID.replace("\n", "','");
        setsID = "'" + setsID + "'";

        String query = "UPDATE " + Values.calendar + " SET " + Values.reminder_enabled + " = '" + reminderEnabled + "' WHERE " + Values.set_id + " IN (" + setsID + ");";

        db.execSQL(query);
        db.close();
    }

    public void updateNotificationEnabled(String setID, boolean isEnabled) {
        int notificationEnabled;
        if (isEnabled) {
            notificationEnabled = 1;
        } else {
            notificationEnabled = 0;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + Values.calendar + " SET " + Values.notifications_mode + " = '" + notificationEnabled + "' WHERE " + Values.set_id + " = '" + setID + "';";
        db.execSQL(query);
        db.close();
    }

    public void updateTestDate(String setID, String deadline) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + Values.calendar + " SET " + Values.deadline + " = '" + deadline + "' WHERE " + Values.set_id + " = '" + setID + "';";

        db.execSQL(query);
        db.close();
    }


    public void updateReminderDaysBefore(String setID, String reminderDaysBefore) {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "UPDATE " + Values.calendar + " SET " + Values.reminder_days_before + " = '" + reminderDaysBefore + "' WHERE " + Values.set_id + " = '" + setID + "';";

        db.execSQL(query);
        db.close();
    }

    public ReminderInfo getInfoAboutReminderFromCalendar(String setID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + Values.deadline + ", " + Values.reminder_days_before + ", " + Values.reminder_enabled + " FROM " + Values.calendar + " WHERE " + Values.set_id + " = '" + setID + "';";
        Cursor cursorReminder = db.rawQuery(query, null);

        ReminderInfo reminderInfo = new ReminderInfo();

        if (cursorReminder.moveToFirst()) {
            reminderInfo.setDeadline(cursorReminder.getString(0));
            reminderInfo.setReminderDaysBefore(cursorReminder.getInt(1));
            reminderInfo.setEnabled(cursorReminder.getInt(2));
        }

        cursorReminder.close();
        db.close();
        return reminderInfo;
    }

    public List<ReminderInfo> getInfoAboutAllReminders(int orderOption) {
        String order = "";
        if (orderOption == Values.ORDER_BY_ID_ASC) {
            order = "ORDER BY " + Values.set_id + " ASC";
        } else if (orderOption == Values.ORDER_BY_ID_DESC) {
            order = "ORDER BY " + Values.set_id + " DESC";
        }

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + Values.set_id + ", " + Values.deadline + ", " + Values.reminder_days_before + ", " + Values.reminder_enabled + " FROM " + Values.calendar + " " + order + ";";
        Cursor cursorReminder = db.rawQuery(query, null);
        List<ReminderInfo> reminderInfos = new ArrayList<>();
        if (cursorReminder.moveToFirst()) {
            do {
                ReminderInfo reminderInfo = new ReminderInfo();
                reminderInfo.setSetID(cursorReminder.getString(0));
                reminderInfo.setDeadline(cursorReminder.getString(1));
                reminderInfo.setReminderDaysBefore(cursorReminder.getInt(2));
                reminderInfo.setEnabled(cursorReminder.getInt(3));
                reminderInfos.add(reminderInfo);

            } while (cursorReminder.moveToNext());
        }

        cursorReminder.close();
        db.close();
        return reminderInfos;
    }

    public List<NotificationInfo> getInfoAboutAllNotifications(int orderOption) {
        String order = "";
        if (orderOption == Values.ORDER_BY_ID_ASC) {
            order = "ORDER BY " + Values.set_id + " ASC";
        } else if (orderOption == Values.ORDER_BY_ID_DESC) {
            order = "ORDER BY " + Values.set_id + " DESC";
        }

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + Values.set_id + ", " +
                Values.notifications_days_of_week + ", " +
                Values.notifications_hours + ", " +
                Values.notifications_mode + " FROM " +
                Values.calendar + " " + order + ";";

        Cursor cursor = db.rawQuery(query, null);
        List<NotificationInfo> notificationInfos = new ArrayList<>();

        if(cursor.moveToFirst()) {
            do {
                NotificationInfo notificationInfo = new NotificationInfo();
                notificationInfo.setSetID(cursor.getString(0));
                notificationInfo.setDaysOfWeek(cursor.getString(1));
                notificationInfo.setHours(cursor.getString(2));
                notificationInfo.setMode(cursor.getInt(3));
                notificationInfos.add(notificationInfo);
            } while(cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return notificationInfos;
    }

    public List<ReminderInfo> listOfEnabledReminders() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + Values.set_id + ", " + Values.deadline + ", " + Values.reminder_days_before + " FROM " + Values.calendar + " WHERE " + Values.reminder_enabled + " = '1';";
        Cursor cursorReminder = db.rawQuery(query, null);
        List<ReminderInfo> reminderInfos = new ArrayList<>();
        if (cursorReminder.moveToFirst()) {
            do {
                ReminderInfo reminderInfo = new ReminderInfo();
                reminderInfo.setSetID(cursorReminder.getString(0));
                reminderInfo.setDeadline(cursorReminder.getString(1));
                reminderInfo.setReminderDaysBefore(cursorReminder.getInt(2));
                reminderInfos.add(reminderInfo);

            } while (cursorReminder.moveToNext());
        }

        cursorReminder.close();
        db.close();
        return reminderInfos;
    }

    public String setNameResolver(String setID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + Values.set_name + " FROM " + Values.sets_info + " WHERE " + Values.set_id + " = '" + setID + "';";
        Cursor cursor = db.rawQuery(query, null);

        String setName = null;
        if (cursor.moveToFirst()) {
            setName = cursor.getString(0);
        }

        cursor.close();
        db.close();
        return setName;
    }

    public void createSet(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        String CREATE_SET = "CREATE TABLE IF NOT EXISTS " + name + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Values.question + " TEXT, " +
                Values.answer + " TEXT, " +
                Values.image + " TEXT, " +
                Values.good_answers + " INTEGER DEFAULT 0, " +
                Values.wrong_answers + " INTEGER DEFAULT 0);";
        db.execSQL(CREATE_SET);
        db.close();
    }

    public List<SetItemContent> getFullSetContent(String setID) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<SetItemContent> setItemContents = new ArrayList<>();
        String query = "SELECT * FROM " + setID + ";";
        Cursor cursor = db.rawQuery(query, null);
        SetItemContent setItemContent;
        if (cursor.moveToFirst()) {
            do {
                setItemContent = new SetItemContent();
                setItemContent.setId(cursor.getInt(0));
                setItemContent.setQuestion(cursor.getString(1));
                setItemContent.setAnswer(cursor.getString(2));
                setItemContent.setImage(cursor.getString(3));
                setItemContent.setGoodAnswers(cursor.getInt(4));
                setItemContent.setWrongAnswers(cursor.getInt(5));

                setItemContents.add(setItemContent);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return setItemContents;
    }

    public List<SetFullInfo> getAllSetsFullInfo() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<SetFullInfo> setFullInfoList = new ArrayList<>();
        String query = "SELECT * FROM " + Values.sets_info + ";";
        Cursor cursor = db.rawQuery(query, null);
        SetFullInfo setFullInfo;

        if (cursor.moveToFirst()) {
            do {
                setFullInfo = new SetFullInfo();
                setFullInfo.setSet_id(cursor.getString(0));
                setFullInfo.setSet_name(cursor.getString(1));
                setFullInfo.setCreation_date(cursor.getString(2));
                setFullInfo.setEnabled(cursor.getInt(3));
                setFullInfo.setIgnore_chars(cursor.getInt(4));
                setFullInfo.setFirst_lang(cursor.getString(5));
                setFullInfo.setSecond_lang(cursor.getString(6));
                setFullInfo.setGood_answers(cursor.getInt(7));
                setFullInfo.setWrong_answers(cursor.getInt(8));

                setFullInfoList.add(setFullInfo);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return setFullInfoList;
    }

    public void addSetToSetsInfoAndCalendar(SingleSetInfo List) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Values.set_id, List.getSetID());
        contentValues.put(Values.set_name, List.getSetName());
        contentValues.put(Values.creation_date, List.getCreateDate());
        contentValues.put(Values.enabled, List.getIsEnabled());
        contentValues.put(Values.ignore_chars, List.getIgnoreChars());
        contentValues.put(Values.first_lang, List.getFirstLanguage());
        contentValues.put(Values.second_lang, List.getSecondLanguage());
        contentValues.put(Values.good_answers, List.getGoodAnswers());
        contentValues.put(Values.wrong_answers, List.getWrongAnswers());
        db.insert(Values.sets_info, null, contentValues);
        db.close();

        addSetToCalendar(List.getSetID());
    }

    public void addSetToCalendar(String setID) {
        setID = "'" + setID + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "INSERT INTO " + Values.calendar + " (" + Values.set_id + ") VALUES (" + setID + ");";
        db.execSQL(query);
        db.close();
    }

    public void setSetName(String name, String SetID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Values.set_name, name);

        db.update(Values.sets_info, contentValues, Values.set_id + "=?", new String[]{SetID});
        db.close();
    }

    public int addEmptyItemToSet(String SetID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("question", "");
        values.put("answer", "");
        values.put("image", "");

        db.insert(SetID, null, values);

        String query = "SELECT last_insert_rowid()";
        Cursor cursor = db.rawQuery(query, null);

        int lastItemID = 0;

        if(cursor.moveToFirst()) {
            lastItemID = cursor.getInt(0);
        }

        db.close();
        return lastItemID;
    }

    public SingleSetInfo singleSetInfo(String setID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + Values.sets_info + " WHERE " + Values.set_id + " = '" + setID + "';";
        Cursor cursor = db.rawQuery(query, null);

        SingleSetInfo singleSetInfo = new SingleSetInfo();

        if (cursor != null) {
            cursor.moveToFirst();

            singleSetInfo.setSetID(cursor.getString(0));
            singleSetInfo.setSetName(cursor.getString(1));
            singleSetInfo.setCreateDate(cursor.getString(2));
            singleSetInfo.setIsEnabled(cursor.getInt(3));
            singleSetInfo.setIgnoreChars(cursor.getInt(4));
            singleSetInfo.setFirstLanguage(cursor.getString(5));
            singleSetInfo.setSecondLanguage(cursor.getString(6));
            singleSetInfo.setGoodAnswers(cursor.getInt(7));
            singleSetInfo.setWrongAnswers(cursor.getInt(8));

            cursor.close();
        }

        db.close();
        return singleSetInfo;
    }

    public List<SingleSetInfo> allSetsInfo(int orderOption) {
        String order = "";

        if (orderOption == Values.ORDER_BY_GOOD_ANSWERS_RATIO) {
            order = "ORDER BY CAST(" + Values.good_answers + " AS FLOAT)/(" + Values.good_answers + "+" + Values.wrong_answers + ") DESC";
        } else if (orderOption == Values.ORDER_BY_WRONG_ANSWERS_RATIO) {
            order = "ORDER BY CAST(" + Values.wrong_answers + " AS FLOAT)/(" + Values.good_answers + "+" + Values.wrong_answers + ") DESC";
        } else if (orderOption == Values.ORDER_BY_ID_ASC) {
            order = "ORDER BY " + Values.set_id + " ASC";
        } else if (orderOption == Values.ORDER_BY_ID_DESC) {
            order = "ORDER BY " + Values.set_id + " DESC";
        }

        List<SingleSetInfo> ALL = new LinkedList<>();
        String query = "SELECT * FROM " + Values.sets_info + " " + order + ";";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        SingleSetInfo list;

        if (cursor.moveToFirst()) {
            do {
                list = new SingleSetInfo();
                list.setSetID(cursor.getString(0));
                list.setSetName(cursor.getString(1));
                list.setCreateDate(cursor.getString(2));
                list.setIsEnabled(cursor.getInt(3));
                list.setIgnoreChars(cursor.getInt(4));
                list.setFirstLanguage(cursor.getString(5));
                list.setSecondLanguage(cursor.getString(6));
                list.setGoodAnswers(cursor.getInt(7));
                list.setWrongAnswers(cursor.getInt(8));
                ALL.add(list);
            } while (cursor.moveToNext());
        }

        db.close();
        cursor.close();
        return ALL;
    }

    List<String> getAllSetsIDs() {
        SQLiteDatabase DB = this.getReadableDatabase();
        String query = "SELECT " + Values.set_id + " FROM " + Values.sets_info + ";";
        Cursor cursor = DB.rawQuery(query, null);

        List<String> allSetsIDs = new ArrayList<>();

        if(cursor.moveToFirst()) {
            do {
                allSetsIDs.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        return allSetsIDs;
    }

    List<SingleSetInfo> allEnabledSetsInfo() {
        List<SingleSetInfo> ALL = new LinkedList<>();
        String query = "SELECT * FROM " + Values.sets_info + " WHERE " + Values.enabled + " = 1;";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                SingleSetInfo list = new SingleSetInfo();
                list.setSetID(cursor.getString(0));
                list.setSetName(cursor.getString(1));
                list.setCreateDate(cursor.getString(2));
                list.setIsEnabled(cursor.getInt(3));
                list.setIgnoreChars(cursor.getInt(4));
                list.setFirstLanguage(cursor.getString(5));
                list.setSecondLanguage(cursor.getString(6));
                list.setGoodAnswers(cursor.getInt(7));
                list.setWrongAnswers(cursor.getInt(8));
                ALL.add(list);
            } while (cursor.moveToNext());
        }

        db.close();
        cursor.close();
        return ALL;
    }

    void addItemToSetWithValues(String SetID, String question, String answer, String image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("question", question);
        values.put("answer", answer);
        values.put("image", image);

        db.insert(SetID, null, values);
        db.close();
    }

    public void insertValueToSetByID(String SetID, int ID, String column, String what) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(column, what);
        db.update(SetID, values, "id=?", new String[]{String.valueOf(ID)});
        db.close();
    }

    public String getValueByCondition(String selectColumn, String table, String column, String value) {
        String returnValue = "";
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + selectColumn + " FROM " + table + " WHERE " + column + " = '" + value + "';";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            returnValue = cursor.getString(0);
        }
        db.close();
        cursor.close();

        return returnValue;
    }

    public void deleteImage(String SetID, String imageName) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + SetID + " SET " + Values.image + "='' WHERE " + Values.image + "='" + imageName + "';";
        db.execSQL(query);
        db.close();
    }

    public void deleteItemFromSet(String SetID, int index) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + SetID + " WHERE id='" + index + "';";
        db.execSQL(query);
        db.close();
    }

    public ArrayList<String> getAllImages(String SetID) {
        ArrayList<String> allImages = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + Values.image + " FROM " + SetID;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                String image = cursor.getString(0);
                allImages.add(image);
            }
            while (cursor.moveToNext());
        }
        db.close();
        cursor.close();

        return allImages;
    }

    public String getAnswer(String SetID, int index) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + Values.answer + " FROM " + SetID + " WHERE id='" + index + "';";
        Cursor cursor = db.rawQuery(query, null);
        String answers = "";

        if (cursor.moveToFirst()) {
            answers = cursor.getString(0);
        }
        db.close();
        cursor.close();
        return answers;
    }

    public void updateIgnoreChars(String setID, int ignore) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + Values.sets_info + " SET " + Values.ignore_chars + "=" + ignore + " WHERE " + Values.set_id + "='" + setID + "';";
        db.execSQL(query);
        db.close();
    }

    public void deleteSetFromDatabase(String setID) {
        SQLiteDatabase db = this.getWritableDatabase();
        String DELETE_NAME = "DELETE FROM " + Values.sets_info + " WHERE " + Values.set_id + " =" + "'" + setID + "';";
        db.execSQL(DELETE_NAME);
        db.close();

        deleteSetFromCalendar(setID);
    }

    public void deleteSetFromCalendar(String setID) {
        SQLiteDatabase db = this.getWritableDatabase();
        String DELETE_NAME = "DELETE FROM " + Values.calendar + " WHERE " + Values.set_id + " =" + "'" + setID + "';";
        db.execSQL(DELETE_NAME);
        db.close();
    }

    public void deleteSet(String setID) {
        SQLiteDatabase db = this.getWritableDatabase();
        String DELETE_SET = "DROP TABLE " + setID + ";";
        db.execSQL(DELETE_SET);
        db.close();
    }

    public List<SetSingleItem> allItemsInSet(String setID, int orderOption) {
        String order = "";
        if (orderOption == Values.ORDER_BY_GOOD_ANSWERS_DESC) {
            order = " ORDER BY " + Values.good_answers + " DESC";
        } else if (orderOption == Values.ORDER_BY_WRONG_ANSWERS_DESC) {
            order = " ORDER BY " + Values.wrong_answers + " DESC";
        } else if (orderOption == Values.ORDER_BY_ID_ASC) {
            order = " ORDER BY id ASC";
        } else if (orderOption == Values.ORDER_BY_ID_DESC) {
            order = " ORDER BY id DESC";
        }
        List<SetSingleItem> ALL = new LinkedList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + setID + order;
        String query1 = "SELECT " + Values.set_name + " FROM " + Values.sets_info + " WHERE " + Values.set_id + " = '" + setID + "';";
        String setName = "";
        Cursor cursor = db.rawQuery(query1, null);
        if (cursor.moveToFirst()) {
            setName = cursor.getString(0);
        }

        cursor.close();
        cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                SetSingleItem list = new SetSingleItem();
                list.setSetName(setName);
                list.setSetID(setID);
                list.setItemID(cursor.getInt(0));
                list.setQuestion(cursor.getString(1));
                list.setAnswer(cursor.getString(2));
                list.setImage(cursor.getString(3));
                list.setGoodAnswers(cursor.getInt(4));
                list.setWrongAnswers(cursor.getInt(5));
                ALL.add(list);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return ALL;
    }

    public ArrayList<String> getSingleColumnFromSetInfo(String column) {
        ArrayList<String> list = new ArrayList<>();
        String query = "SELECT " + column + " FROM " + Values.sets_info + ";";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        db.close();
        cursor.close();
        return list;
    }

    public void insertSetToDatabase(String setID, ArrayList<String> questions, ArrayList<String> answers, ArrayList<String> images) {
        SQLiteDatabase db = this.getWritableDatabase();

        if (images == null) {
            for (int i = 0; i < questions.size(); i++) {
                ContentValues contentValues = new ContentValues();

                contentValues.put(Values.question, questions.get(i));
                contentValues.put(Values.answer, answers.get(i));
                contentValues.put(Values.image, "");

                db.insert(setID, null, contentValues);
            }
        } else {
            for (int i = 0; i < questions.size(); i++) {
                ContentValues contentValues = new ContentValues();

                contentValues.put(Values.question, questions.get(i));
                contentValues.put(Values.answer, answers.get(i));
                contentValues.put(Values.image, images.get(i));

                db.insert(setID, null, contentValues);
            }
        }

        db.close();
    }

    public void swapQuestionsWithAnswers(String setID) {
        String query = "UPDATE " + setID + " SET question = answer, answer = question;";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);
        db.close();
    }

    public void copyQuestionsAndAnswersToAnotherTable(String copySetID, String pasteSetID) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "INSERT INTO " + pasteSetID + " (" + Values.question + ", " + Values.answer + ", " + Values.image + ")" +
                " SELECT " + Values.question + ", " + Values.answer + ", " + Values.image + " FROM " + copySetID + " WHERE " + Values.question + " != '' OR " + Values.answer + " != '';";
        db.execSQL(query);
        db.close();
    }

    public int itemsInSetCount(String setID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + setID;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            return cursor.getInt(0);
        }

        cursor.close();
        db.close();
        return 0;
    }

    public List<String> getSingleQuestionAndAnswer(String SetID, int index) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<String> questionAndAnswer = null;

        String query = "SELECT " + Values.question + ", " + Values.answer + " FROM " + SetID + " WHERE id=" + index + ";";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            questionAndAnswer = new ArrayList<>();
            do {
                questionAndAnswer.add(cursor.getString(0));
                questionAndAnswer.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }

        db.close();
        cursor.close();
        return questionAndAnswer;
    }

    public void increaseValueInSetsInfo(String setID, String column, int value) {
        SQLiteDatabase db = this.getWritableDatabase();
        String command = "UPDATE " + Values.sets_info + " SET " + column + " = " + column + " + " + value + " WHERE " + Values.set_id + " = '" + setID + "';";
        db.execSQL(command);
        db.close();
    }

    public void increaseValueInSet(String setID, int itemID, String column, int value) {
        SQLiteDatabase db = this.getWritableDatabase();
        String command = "UPDATE " + setID + " SET " + column + " = " + column + " + " + value + " WHERE id = " + itemID + ";";
        db.execSQL(command);
        db.close();
    }

    public List<SetStats> selectSetsStatsInfo(int orderOption) {
        String order = "";

        if (orderOption == Values.ORDER_BY_GOOD_ANSWERS_RATIO) {
            order = "ORDER BY CAST(" + Values.good_answers + " AS FLOAT)/(" + Values.good_answers + "+" + Values.wrong_answers + ") DESC";
        } else if (orderOption == Values.ORDER_BY_WRONG_ANSWERS_RATIO) {
            order = "ORDER BY CAST(" + Values.wrong_answers + " AS FLOAT)/(" + Values.good_answers + "+" + Values.wrong_answers + ") DESC";
        } else if (orderOption == Values.ORDER_BY_ID_ASC) {
            order = "ORDER BY " + Values.set_id + " ASC";
        } else if (orderOption == Values.ORDER_BY_ID_DESC) {
            order = "ORDER BY " + Values.set_id + " DESC";
        }

        List<SetStats> setStats = new LinkedList<>();
        String query = "SELECT " + Values.set_id + ", " +
                Values.set_name + ", " +
                Values.good_answers + ", " +
                Values.wrong_answers + " FROM " +
                Values.sets_info + " " + order + ";";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                SetStats stats = new SetStats();
                stats.setSetID(cursor.getString(0));
                stats.setName(cursor.getString(1));
                stats.setGoodAnswers(cursor.getInt(2));
                stats.setWrongAnswers(cursor.getInt(3));
                setStats.add(stats);
            } while (cursor.moveToNext());
        }

        db.close();
        cursor.close();

        return setStats;
    }

    public int columnSum(String setID, String column) {
        int sum = -1;
        String query = "SELECT SUM(" + column + ") FROM " + setID + ";";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            sum = cursor.getInt(0);
        }

        db.close();
        cursor.close();
        return sum;
    }

    public List<FastLearningSetsListItem> setsIdAndNameList() {
        List<FastLearningSetsListItem> setIdAndName = new ArrayList<>();
        String query = "SELECT " + Values.set_id +
                ", " + Values.set_name +
                ", (good_answers + wrong_answers)" + " FROM " +
                Values.sets_info + " ORDER BY " +
                Values.set_id + " DESC;";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        FastLearningSetsListItem listItem;

        if (cursor.moveToFirst()) {
            do {
                listItem = new FastLearningSetsListItem(cursor.getString(0), cursor.getString(1), cursor.getInt(2));
                setIdAndName.add(listItem);
            } while (cursor.moveToNext());
        }

        db.close();
        cursor.close();
        return setIdAndName;
    }

    public FastLearningSetsListItem singleSetIdNameAndStats(String setID) {
        FastLearningSetsListItem item = null;
        String query = "SELECT " + Values.set_name + ", "
                + " (" + Values.good_answers
                + "+" + Values.wrong_answers + ") FROM "
                + Values.sets_info + " WHERE "
                + Values.set_id + "='" + setID + "';";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            item = new FastLearningSetsListItem(setID, cursor.getString(0), cursor.getInt(1));
        }

        db.close();
        cursor.close();

        return item;
    }

    public List<SetSingleItem> getItemsForFastLearning(List<FastLearningSetsListItem> setsList) {
        List<SetSingleItem> singleItems = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        for (int i = 0; i < setsList.size(); i++) {
            String query = "SELECT " + Values.set_name + " FROM " + Values.sets_info + " WHERE " + Values.set_id + " = '" + setsList.get(i).getSetID() + "';";
            Cursor cursor = db.rawQuery(query, null);
            String setName = "";

            if (cursor.moveToFirst()) {
                setName = cursor.getString(0);
            }

            cursor.close();

            String query1 = "SELECT " + Values.question + ", "
                    + Values.answer + ", "
                    + Values.image + ", "
                    + Values.good_answers + ", "
                    + Values.wrong_answers + " FROM "
                    + setsList.get(i).getSetID() + ";";
            cursor = db.rawQuery(query1, null);

            if (cursor.moveToFirst()) {
                do {
                    SetSingleItem fastLearningSingleItem = new SetSingleItem();
                    fastLearningSingleItem.setSetID(setsList.get(i).getSetID());
                    fastLearningSingleItem.setSetName(setName);
                    fastLearningSingleItem.setQuestion(cursor.getString(0));
                    fastLearningSingleItem.setAnswer(cursor.getString(1));
                    fastLearningSingleItem.setImage(cursor.getString(2));
                    fastLearningSingleItem.setGoodAnswers(cursor.getInt(3));
                    fastLearningSingleItem.setWrongAnswers(cursor.getInt(4));
                    singleItems.add(fastLearningSingleItem);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        db.close();
        return singleItems;
    }

    public void updateTable(String TABLE, String WHAT, String WHERE) {
        SQLiteDatabase db = this.getWritableDatabase();

        String Command;

        if (!WHERE.equals("")) {
            Command = "UPDATE " + TABLE + " SET " + WHAT + " WHERE " + WHERE + ";";
        } else {
            Command = "UPDATE " + TABLE + " SET " + WHAT + ";";
        }

        db.execSQL(Command);
        db.close();
    }

    public void ResetEnabled() {
        SQLiteDatabase db = this.getWritableDatabase();
        String Command;

        Command = "UPDATE " + Values.sets_info + " SET " + Values.enabled + " = 1;";

        db.execSQL(Command);
        db.close();
    }
}
