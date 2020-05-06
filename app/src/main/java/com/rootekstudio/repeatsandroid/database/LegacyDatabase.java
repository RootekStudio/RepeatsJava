package com.rootekstudio.repeatsandroid.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class LegacyDatabase extends SQLiteOpenHelper {
    public LegacyDatabase(Context context) {
        super(context, "repeats", null, 4);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        String CREATE_TITLETABLE = "CREATE TABLE IF NOT EXISTS TitleTable (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, TableName TEXT, CreateDate TEXT, IsEnabled TEXT, " +
//                "Avatar TEXT, IgnoreChars TEXT, firstLanguage TEXT, secondLanguage TEXT, goodAnswers INTEGER DEFAULT 0, wrongAnswers INTEGER DEFAULT 0, allAnswers INTEGER DEFAULT 0)";
//        db.execSQL(CREATE_TITLETABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public List<SingleSetInfo> allSetsInfo() {
        String firstLanguage;
        String secondLanguage;

        if(Locale.getDefault().toString().equals("pl_PL")) {
            firstLanguage = "pl_PL";
            secondLanguage = "en_GB";
        }
        else {
            firstLanguage = "en_US";
            secondLanguage = "es_ES";
        }

        SQLiteDatabase DB = this.getReadableDatabase();
        List<SingleSetInfo> setInfo = new ArrayList<>();
        SingleSetInfo singleSetInfo;
        if(DB.getVersion() == 4) {
            String query = "SELECT TableName, title, CreateDate, IsEnabled, IgnoreChars, firstLanguage, secondLanguage, goodAnswers, wrongAnswers FROM TitleTable;";
            Cursor cursor = DB.rawQuery(query, null);
            if(cursor.moveToFirst()) {
                do {
                    singleSetInfo = new SingleSetInfo();
                    singleSetInfo.setSetID(cursor.getString(0));
                    singleSetInfo.setSetName(cursor.getString(1));
                    singleSetInfo.setCreateDate(cursor.getString(2));
                    singleSetInfo.setIsEnabled(Boolean.getBoolean(cursor.getString(3)) ? 1 : 0);
                    singleSetInfo.setIgnoreChars(Boolean.getBoolean(cursor.getString(4)) ? 1 : 0);
                    singleSetInfo.setFirstLanguage(cursor.getString(5));
                    singleSetInfo.setSecondLanguage(cursor.getString(6));
                    singleSetInfo.setGoodAnswers(cursor.getInt(7));
                    singleSetInfo.setWrongAnswers(cursor.getInt(8));

                    setInfo.add(singleSetInfo);
                }while(cursor.moveToNext());
            }

            cursor.close();
            DB.close();
        }
        else if(DB.getVersion() == 3) {
            String query = "SELECT TableName, title, CreateDate, IsEnabled, IgnoreChars, firstLanguage, secondLanguage FROM TitleTable;";
            Cursor cursor = DB.rawQuery(query, null);
            if(cursor.moveToFirst()) {
                do {
                    singleSetInfo = new SingleSetInfo();
                    singleSetInfo.setSetID(cursor.getString(0));
                    singleSetInfo.setSetName(cursor.getString(1));
                    singleSetInfo.setCreateDate(cursor.getString(2));
                    singleSetInfo.setIsEnabled(Boolean.getBoolean(cursor.getString(3)) ? 1 : 0);
                    singleSetInfo.setIgnoreChars(Boolean.getBoolean(cursor.getString(4)) ? 1 : 0);
                    singleSetInfo.setFirstLanguage(cursor.getString(5));
                    singleSetInfo.setSecondLanguage(cursor.getString(6));
                    singleSetInfo.setGoodAnswers(0);
                    singleSetInfo.setWrongAnswers(0);

                    setInfo.add(singleSetInfo);
                }while(cursor.moveToNext());
            }

            cursor.close();
            DB.close();
        }

        else if(DB.getVersion() == 2) {
            String query = "SELECT TableName, title, CreateDate, IsEnabled, IgnoreChars FROM TitleTable;";
            Cursor cursor = DB.rawQuery(query, null);

            if(cursor.moveToFirst()) {
                do {
                    singleSetInfo = new SingleSetInfo();
                    singleSetInfo.setSetID(cursor.getString(0));
                    singleSetInfo.setSetName(cursor.getString(1));
                    singleSetInfo.setCreateDate(cursor.getString(2));
                    singleSetInfo.setIsEnabled(Boolean.getBoolean(cursor.getString(3)) ? 1 : 0);
                    singleSetInfo.setIgnoreChars(Boolean.getBoolean(cursor.getString(4)) ? 1 : 0);
                    singleSetInfo.setFirstLanguage(firstLanguage);
                    singleSetInfo.setSecondLanguage(secondLanguage);
                    singleSetInfo.setGoodAnswers(0);
                    singleSetInfo.setWrongAnswers(0);

                    setInfo.add(singleSetInfo);
                }while(cursor.moveToNext());
            }

            cursor.close();
            DB.close();

        } else if(DB.getVersion() == 1) {
            String query = "SELECT TableName, title, CreateDate, IsEnabled FROM TitleTable;";
            Cursor cursor = DB.rawQuery(query, null);

            if(cursor.moveToFirst()) {
                do {
                    singleSetInfo = new SingleSetInfo();
                    singleSetInfo.setSetID(cursor.getString(0));
                    singleSetInfo.setSetName(cursor.getString(1));
                    singleSetInfo.setCreateDate(cursor.getString(2));
                    singleSetInfo.setIsEnabled(Boolean.getBoolean(cursor.getString(3)) ? 1 : 0);
                    singleSetInfo.setIgnoreChars(0);
                    singleSetInfo.setFirstLanguage(firstLanguage);
                    singleSetInfo.setSecondLanguage(secondLanguage);
                    singleSetInfo.setGoodAnswers(0);
                    singleSetInfo.setWrongAnswers(0);

                    setInfo.add(singleSetInfo);
                }while(cursor.moveToNext());
            }

            cursor.close();
            DB.close();
        }

        return setInfo;
    }

    public List<SetSingleItem> allItemsInSet(String setID) {
        SQLiteDatabase DB = this.getReadableDatabase();
        List<SetSingleItem> itemsInSet = new ArrayList<>();
        SetSingleItem singleItem;
        if(DB.getVersion() == 4) {
            String query = "SELECT question, answer, image, goodAnswers, wrongAnswers FROM " + setID + " ;";
            Cursor cursor = DB.rawQuery(query, null);

            if(cursor.moveToFirst()) {
                do {
                    singleItem = new SetSingleItem();
                    singleItem.setQuestion(cursor.getString(0));
                    singleItem.setAnswer(cursor.getString(1));
                    singleItem.setImage(cursor.getString(2));
                    singleItem.setGoodAnswers(cursor.getInt(3));
                    singleItem.setWrongAnswers(cursor.getInt(4));

                    itemsInSet.add(singleItem);
                }while(cursor.moveToNext());
            }
            cursor.close();
            DB.close();
        }
        else {
            String query = "SELECT question, answer, image FROM " + setID + " ;";
            Cursor cursor = DB.rawQuery(query, null);

            if(cursor.moveToFirst()) {
                do {
                    singleItem = new SetSingleItem();
                    singleItem.setQuestion(cursor.getString(0));
                    singleItem.setAnswer(cursor.getString(1));
                    singleItem.setImage(cursor.getString(2));
                    singleItem.setGoodAnswers(0);
                    singleItem.setWrongAnswers(0);

                    itemsInSet.add(singleItem);
                }while(cursor.moveToNext());
            }
            cursor.close();
            DB.close();
        }

        return itemsInSet;
    }
}