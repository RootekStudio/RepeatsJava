package com.rootekstudio.repeatsandroid.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.rootekstudio.repeatsandroid.RepeatsListDB;
import com.rootekstudio.repeatsandroid.RepeatsSingleSetDB;
import com.rootekstudio.repeatsandroid.SetStats;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String NAME = "TitleTable";
    private static final String KEY_TITLE = "title";
    private static final String KEY_TNAME = "TableName";
    private static final String KEY_DATE = "CreateDate";
    private static final String KEY_ENABLED = "IsEnabled";
    private static final String KEY_AVATAR = "Avatar";
    private static final String KEY_IGNORE_CHARS = "IgnoreChars";
    private static final String KEY_FIRST_LANGUAGE = "firstLanguage";
    private static final String KEY_SECOND_LANGUAGE = "secondLanguage";
    private static final String KEY_GOOD_ANSWERS = "goodAnswers";
    private static final String KEY_WRONG_ANSWERS = "wrongAnswers";
    private static final String KEY_ALL_ANSWERS = "allAnswers";

    public DatabaseHelper(Context context) {
        super(context, "repeats", null, 4);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TITLETABLE = "CREATE TABLE IF NOT EXISTS TitleTable (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, TableName TEXT, CreateDate TEXT, IsEnabled TEXT, " +
                "Avatar TEXT, IgnoreChars TEXT, firstLanguage TEXT, secondLanguage TEXT, goodAnswers INTEGER DEFAULT 0, wrongAnswers INTEGER DEFAULT 0, allAnswers INTEGER DEFAULT 0)";
        db.execSQL(CREATE_TITLETABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == 1) {
            String command = "ALTER TABLE TitleTable ADD COLUMN IgnoreChars TEXT";
            String command2 = "UPDATE TitleTable SET IgnoreChars='false'";
            db.execSQL(command);
            db.execSQL(command2);
        }

        if(oldVersion == 2) {
            String command = "ALTER TABLE TitleTable ADD COLUMN firstLanguage TEXT";
            String command1 = "ALTER TABLE TitleTable ADD COLUMN secondLanguage TEXT";
            String command2 = "";
            String command3 = "";
            if(Locale.getDefault().toString().equals("pl_PL")) {
                command2 = "UPDATE TitleTable SET firstLanguage='pl_PL'";
                command3 = "UPDATE TitleTable SET secondLanguage='en_GB'";
            }
            else {
                command2 = "UPDATE TitleTable SET firstLanguage='en_US'";
                command3 = "UPDATE TitleTable SET secondLanguage='es_ES'";
            }

            db.execSQL(command);
            db.execSQL(command1);
            db.execSQL(command2);
            db.execSQL(command3);
        }

        if(oldVersion == 3) {
            String command = "ALTER TABLE TitleTable ADD COLUMN goodAnswers INTEGER DEFAULT 0";
            String command1 = "ALTER TABLE TitleTable ADD COLUMN wrongAnswers INTEGER DEFAULT 0";
            String command2 = "ALTER TABLE TitleTable ADD COLUMN allAnswers INTEGER DEFAULT 0";

            db.execSQL(command);
            db.execSQL(command1);
            db.execSQL(command2);

            String query = "SELECT TableName FROM TitleTable";
            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                do {
                    String id = cursor.getString(0);
                    String singleSetCommand = "ALTER TABLE " + id + " ADD COLUMN goodAnswers INTEGER DEFAULT 0";
                    String singleSetCommand1 = "ALTER TABLE " + id + " ADD COLUMN wrongAnswers INTEGER DEFAULT 0";
                    String singleSetCommand2 = "ALTER TABLE " + id + " ADD COLUMN allAnswers INTEGER DEFAULT 0";

                    db.execSQL(singleSetCommand);
                    db.execSQL(singleSetCommand1);
                    db.execSQL(singleSetCommand2);
                }
                while (cursor.moveToNext());
            }
            cursor.close();
        }
    }

    public RepeatsListDB getSingleItemLIST(String setID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM TitleTable WHERE TableName='" + setID + "'";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        RepeatsListDB list = new RepeatsListDB();
        list.setTitle(cursor.getString(1));
        list.setTableName(cursor.getString(2));
        list.setCreateDate(cursor.getString(3));
        list.setIsEnabled(cursor.getString(4));
        list.setAvatar(cursor.getString(5));
        list.setIgnoreChars(cursor.getString(6));
        list.setFirstLanguage(cursor.getString(7));
        list.setSecondLanguage(cursor.getString(8));
        list.setGoodAnswers(cursor.getInt(9));
        list.setWrongAnswers(cursor.getInt(10));
        list.setAllAnswers(cursor.getInt(11));

        db.close();
        cursor.close();
        return list;
    }

    public List<RepeatsListDB> AllItemsLIST() {
        List<RepeatsListDB> ALL = new LinkedList<>();
        String query = "SELECT * FROM " + NAME + " ORDER BY id DESC;";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        RepeatsListDB list;

        if (cursor.moveToFirst()) {
            do {
                list = new RepeatsListDB();
                list.setTitle(cursor.getString(1));
                list.setTableName(cursor.getString(2));
                list.setCreateDate(cursor.getString(3));
                list.setIsEnabled(cursor.getString(4));
                list.setAvatar(cursor.getString(5));
                list.setIgnoreChars(cursor.getString(6));
                list.setFirstLanguage(cursor.getString(7));
                list.setSecondLanguage(cursor.getString(8));
                list.setGoodAnswers(cursor.getInt(9));
                list.setWrongAnswers(cursor.getInt(10));
                list.setAllAnswers(cursor.getInt(11));
                ALL.add(list);
            } while (cursor.moveToNext());
        }

        db.close();
        cursor.close();
        return ALL;
    }

    List<RepeatsListDB> ALLEnabledSets() {
        List<RepeatsListDB> ALL = new LinkedList<>();
        String query = "SELECT * FROM TitleTable WHERE IsEnabled='true'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        RepeatsListDB list;

        if (cursor.moveToFirst()) {
            do {
                list = new RepeatsListDB();
                list.setTitle(cursor.getString(1));
                list.setTableName(cursor.getString(2));
                list.setCreateDate(cursor.getString(3));
                list.setIsEnabled(cursor.getString(4));
                list.setAvatar(cursor.getString(5));
                list.setIgnoreChars(cursor.getString(6));
                list.setFirstLanguage(cursor.getString(7));
                list.setSecondLanguage(cursor.getString(8));
                list.setGoodAnswers(cursor.getInt(9));
                list.setWrongAnswers(cursor.getInt(10));
                list.setAllAnswers(cursor.getInt(11));
                ALL.add(list);
            } while (cursor.moveToNext());
        }

        db.close();
        cursor.close();
        return ALL;
    }

    public void AddItem(String SetID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("question", "");
        values.put("answer", "");
        values.put("image", "");

        db.insert(SetID, null, values);
        db.close();
    }

    void AddItemWithValues(String SetID, String question, String answer, String image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("question", question);
        values.put("answer", answer);
        values.put("image", image);

        db.insert(SetID, null, values);
        db.close();
    }

    public void InsertValueByID(String SetID, int ID, String column, String what) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(column, what);
        db.update(SetID, values, "id=?", new String[]{String.valueOf(ID)});
        db.close();
    }

    public void increaseValueInTitleTable(String setID, String column, int value) {
        SQLiteDatabase db = this.getWritableDatabase();
        String command = "UPDATE TitleTable SET " + column + " = " + column + " + " + value + " WHERE TableName = '" + setID + "'";
        db.execSQL(command);
        db.close();
    }

    public void increaseValueInSet(String setID, int itemID, String column, int value) {
        SQLiteDatabase db = this.getWritableDatabase();
        String command = "UPDATE " + setID + " SET " + column + " = " + column + " + " + value + " WHERE id = " + itemID;
        db.execSQL(command);
        db.close();
    }

    public String getValue(String column, String table, String where) {
        String value = "";
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + column + " FROM " + table + " WHERE " + where;

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            value = cursor.getString(0);
        }
        db.close();
        cursor.close();

        return value;
    }

    public void setTableName(String name, String SetID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", name);

        db.update("TitleTable", values, "TableName=?",new String[]{SetID});
        db.close();
    }

    public void deleteImage(String SetID, String imageName) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + SetID + " SET image='' WHERE image='" + imageName + "'";
        db.execSQL(query);
        db.close();
    }

    public void deleteItem(String SetID, int index) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + SetID + " WHERE id='" + index + "'";
        db.execSQL(query);
        db.close();
    }

    public ArrayList<String> getAllImages(String SetID) {
        ArrayList<String> allImages = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT image FROM " + SetID;
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

    public String getAnswers(String SetID, int index) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT answer FROM " + SetID + " WHERE id='" + index + "'";
        Cursor cursor = db.rawQuery(query, null);
        String answers = "";

        if (cursor.moveToFirst()) {
            answers = cursor.getString(0);
        }
        db.close();
        cursor.close();
        return answers;
    }

    public void ignoreChars(String SetID, String isIgnoring) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE TitleTable SET IgnoreChars='" + isIgnoring + "' WHERE TableName='" + SetID + "'";
        db.execSQL(query);
        db.close();
    }

    public void AddName(RepeatsListDB List) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, List.getitle());
        values.put(KEY_TNAME, List.getTableName());
        values.put(KEY_DATE, List.getCreateDate());
        values.put(KEY_ENABLED, List.getIsEnabled());
        values.put(KEY_AVATAR, List.getAvatar());
        values.put(KEY_IGNORE_CHARS, List.getIgnoreChars());
        values.put(KEY_FIRST_LANGUAGE, List.getFirstLanguage());
        values.put(KEY_SECOND_LANGUAGE, List.getSecondLanguage());
        values.put(KEY_GOOD_ANSWERS, List.getGoodAnswers());
        values.put(KEY_WRONG_ANSWERS, List.getWrongAnswers());
        values.put(KEY_ALL_ANSWERS, List.getAllAnswers());
        db.insert(NAME, null, values);
        db.close();
    }

    public void deleteOneFromList(String Title) {
        SQLiteDatabase db = this.getWritableDatabase();
        String DELETE_NAME = "DELETE FROM TitleTable WHERE TableName =" + "\"" + Title + "\"";
        db.execSQL(DELETE_NAME);
        db.close();
    }


    public void CreateSet(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        String CREATE_SET = "CREATE TABLE IF NOT EXISTS " + name + " (id INTEGER PRIMARY KEY AUTOINCREMENT, question TEXT, answer TEXT, image TEXT, " +
                "goodAnswers INTEGER DEFAULT 0, wrongAnswers INTEGER DEFAULT 0, allAnswers INTEGER DEFAULT 0)";
        db.execSQL(CREATE_SET);
        db.close();
    }

    public void DeleteSet(String SETNAME) {
        SQLiteDatabase db = this.getWritableDatabase();
        String DELETE_SET = "DROP TABLE " + SETNAME;
        db.execSQL(DELETE_SET);
        db.close();
    }

    public List<RepeatsSingleSetDB> AllItemsSET(String SETNAME, int sortOption) {
        String order = "";
        if(sortOption == 0) {
            order = " ORDER BY goodAnswers DESC";
        }
        else if(sortOption == 1) {
            order = " ORDER BY wrongAnswers DESC";
        }
        else if(sortOption == 2) {
            order = " ORDER BY id ASC";
        }
        else if(sortOption == 3) {
            order = " ORDER BY id DESC";
        }
        List<RepeatsSingleSetDB> ALL = new LinkedList<>();
        String query = "SELECT * FROM " + SETNAME + order;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        RepeatsSingleSetDB list;

        if (cursor.moveToFirst()) {
            do {
                list = new RepeatsSingleSetDB();
                list.setID(cursor.getInt(0));
                list.setQuestion(cursor.getString(1));
                list.setAnswer(cursor.getString(2));
                list.setImag(cursor.getString(3));
                list.setGoodAnswers(cursor.getInt(4));
                list.setWrongAnswers(cursor.getInt(5));
                list.setAllAnswers(cursor.getInt(6));
                ALL.add(list);
            } while (cursor.moveToNext());
        }
        db.close();
        cursor.close();
        return ALL;
    }

    public ArrayList<String> getSingleColumn(String column) {
        ArrayList<String> list = new ArrayList<>();
        String query = "SELECT " + column + " FROM TitleTable";
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

    public void UpdateTable(String TABLE, String WHAT, String WHERE) {
        SQLiteDatabase db = this.getWritableDatabase();

        String Command;

        if (!WHERE.equals("")) {
            Command = "UPDATE " + TABLE + " SET " + WHAT + " WHERE " + WHERE;
        } else {
            Command = "UPDATE " + TABLE + " SET " + WHAT;
        }

        db.execSQL(Command);
        db.close();
    }

    public void insertSetToDatabase(String setID, ArrayList<String> questions, ArrayList<String> answers, ArrayList<String> images) {
        SQLiteDatabase db = this.getWritableDatabase();

        if(images == null) {
            for(int i = 0; i < questions.size(); i++) {
                ContentValues values = new ContentValues();

                values.put("question", questions.get(i));
                values.put("answer", answers.get(i));
                values.put("image", "");

                db.insert(setID, null, values);
            }
        }
        else {
            for(int i = 0; i < questions.size(); i++) {
                ContentValues values = new ContentValues();

                values.put("question", questions.get(i));
                values.put("answer", answers.get(i));
                values.put("image", images.get(i));

                db.insert(setID, null, values);
            }
        }

        db.close();
    }

    public List<SetStats> selectSetsStatsInfo(int sortOption) {
        String order = "";

        if(sortOption == 0) {
            order = "ORDER BY CAST(goodAnswers AS FLOAT)/allAnswers DESC";
        }
        else if(sortOption == 1) {
            order = "ORDER BY CAST(wrongAnswers AS FLOAT)/allAnswers DESC";
        }
        else if(sortOption == 2) {
            order = "ORDER BY id ASC";
        }
        else if(sortOption == 3) {
            order = "ORDER BY id DESC";
        }

        List<SetStats> setStats = new LinkedList<>();
        String query = "SELECT title, TableName, goodAnswers, wrongAnswers, allAnswers" + " FROM TitleTable " + order;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst()) {
            do {
                SetStats stats = new SetStats();
                stats.setName(cursor.getString(0));
                stats.setSetID(cursor.getString(1));
                stats.setGoodAnswers(cursor.getInt(2));
                stats.setWrongAnswers(cursor.getInt(3));
                stats.setAllAnswers(cursor.getInt(4));
                setStats.add(stats);
            }while(cursor.moveToNext());
        }

        db.close();
        cursor.close();

        return setStats;
    }

    public int columnSum(String table, String column) {
        int sum = -1;
        String query = "SELECT SUM(" + column + ") FROM " + table;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()) {
            sum = cursor.getInt(0);
        }

        db.close();
        cursor.close();
        return sum;
    }

    public void ResetEnabled() {
        SQLiteDatabase db = this.getWritableDatabase();
        String Command;

        Command = "UPDATE TitleTable SET IsEnabled='true'";

        db.execSQL(Command);
        db.close();
    }
}
