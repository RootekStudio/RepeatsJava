package com.rootekstudio.repeatsandroid.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MigrateDatabase {
    private Context context;

    public MigrateDatabase(Context context) {
        this.context = context;
    }

    public static Boolean oldDBExists() {
        File file = Environment.getDataDirectory();
        String path = "/data/com.rootekstudio.repeatsandroid/databases/repeats";
        File oldDB = new File(file, path);
        return oldDB.exists();
    }

    public void migrateToNewDatabase() {
        RepeatsDatabase newDB = RepeatsDatabase.getInstance(context);
        LegacyDatabase oldDB = new LegacyDatabase(context);

        List<SingleSetInfo> setsInfo = oldDB.allSetsInfo();

        SQLiteDatabase writableNewDB = newDB.getWritableDatabase();

        for(int i = 0; i < setsInfo.size(); i++) {
            SingleSetInfo setInfo = setsInfo.get(i);
            String setID = setInfo.getSetID();

            ContentValues contentValues = new ContentValues();
            contentValues.put(Values.set_id, setID);
            contentValues.put(Values.set_name, setInfo.getSetName());
            contentValues.put(Values.creation_date, getCreationDateInNewFormat(setInfo.getCreateDate()));
            contentValues.put(Values.ignore_chars, setInfo.getIgnoreChars());
            contentValues.put(Values.first_lang, setInfo.getFirstLanguage());
            contentValues.put(Values.second_lang, setInfo.getSecondLanguage());
            contentValues.put(Values.good_answers, setInfo.getGoodAnswers());
            contentValues.put(Values.wrong_answers, setInfo.getWrongAnswers());

            writableNewDB.insert(Values.sets_info, null, contentValues);

            String calendarQuery = "INSERT INTO " + Values.calendar + " (" + Values.set_id + ") VALUES ('" + setID + "');";
            writableNewDB.execSQL(calendarQuery);

            String CREATE_SET = "CREATE TABLE IF NOT EXISTS " + setID + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    Values.question + " TEXT, " +
                    Values.answer + " TEXT, " +
                    Values.image + " TEXT, " +
                    Values.good_answers + " INTEGER DEFAULT 0, " +
                    Values.wrong_answers + " INTEGER DEFAULT 0);";
            writableNewDB.execSQL(CREATE_SET);

            List<SetSingleItem> setContent = oldDB.allItemsInSet(setID);

            for(int j = 0; j < setContent.size(); j++) {
                SetSingleItem singleItem = setContent.get(j);
                ContentValues contentValuesSet = new ContentValues();
                contentValuesSet.put(Values.question, singleItem.getQuestion());
                contentValuesSet.put(Values.answer, singleItem.getAnswer());
                contentValuesSet.put(Values.image, singleItem.getImage());
                contentValuesSet.put(Values.good_answers, singleItem.getGoodAnswers());
                contentValuesSet.put(Values.wrong_answers, singleItem.getWrongAnswers());

                writableNewDB.insert(setID, null, contentValuesSet);
            }
        }

        oldDB.close();
        writableNewDB.close();
        context.deleteDatabase("repeats");
    }

    private String getCreationDateInNewFormat(String creationDate) {
        String newDate = "";

        SimpleDateFormat oldDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        try {
            Date date = oldDateFormat.parse(creationDate);
            Calendar calendar = Calendar.getInstance();
            assert date != null;
            calendar.setTime(date);

            int yearInt = calendar.get(Calendar.YEAR);
            int monthInt = calendar.get(Calendar.MONTH) + 1;
            int dayInt = calendar.get(Calendar.DAY_OF_MONTH);

            String month;
            String day;

            if(monthInt < 10) {
                month = "0" + monthInt;
            }
            else {
                month = String.valueOf(monthInt);
            }

            if(dayInt < 10) {
                day = "0" + dayInt;
            }
            else {
                day = String.valueOf(dayInt);
            }

            newDate = yearInt + "-" + month  + "-" + day + " 00:00:00";

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return newDate;
    }
}
