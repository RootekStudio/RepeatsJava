package com.rootekstudio.repeatsandroid;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.LinkedList;
import java.util.List;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper
{
    private static final String NAME = "TitleTable";
    private static final String KEY_TITLE = "title";
    private static final String KEY_TNAME = "TableName";
    private static final String KEY_DATE = "CreateDate";
    private static final String KEY_ENABLED = "IsEnabled";
    private static final String KEY_AVATAR = "Avatar";
    private static final String[] COLUMNS = { KEY_TITLE, KEY_TNAME, KEY_DATE, KEY_ENABLED, KEY_AVATAR };

    private static final String KEY_Q = "question";
    private static final String KEY_A = "answer";
    private static final String KEY_I = "image";
    private static final String[] COLUMNS2 = { KEY_Q, KEY_A, KEY_I};


    public DatabaseHelper(Context context)
    {
        super (context, "repeats", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String CREATE_TITLETABLE = "CREATE TABLE IF NOT EXISTS TitleTable (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, TableName TEXT, CreateDate TEXT, IsEnabled TEXT, Avatar TEXT)";
        db.execSQL(CREATE_TITLETABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }

    public RepeatsListDB getSingleItemLIST (RepeatsListDB List)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(NAME, COLUMNS, "id = ?", new String[]{String.valueOf(List.getitle())}, null, null, null, null);

        if(cursor != null)
        {
            cursor.moveToFirst();
        }

        RepeatsListDB list = new RepeatsListDB();
        list.setTitle(cursor.getString(1));
        list.setTableName(cursor.getString(2));
        list.setCreateDate(cursor.getString(3));
        list.setIsEnabled(cursor.getString(4));
        list.setAvatar(cursor.getString(5));

        return list;
    }

    public List<RepeatsListDB> AllItemsLIST()
    {
        List<RepeatsListDB> ALL = new LinkedList<RepeatsListDB>();
        String query = "SELECT * FROM " + NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        RepeatsListDB list = null;

        if(cursor.moveToFirst())
        {
            do {
                list = new RepeatsListDB();
                list.setTitle(cursor.getString(1));
                list.setTableName(cursor.getString(2));
                list.setCreateDate(cursor.getString(3));
                list.setIsEnabled(cursor.getString(4));
                list.setAvatar(cursor.getString(5));
                ALL.add(list);
            } while (cursor.moveToNext());
        }
        return ALL;
    }

    public void AddName(RepeatsListDB List)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, List.getitle());
        values.put(KEY_TNAME, List.getTableName());
        values.put(KEY_DATE, List.getCreateDate());
        values.put(KEY_ENABLED, List.getIsEnabled());
        values.put(KEY_AVATAR, List.getAvatar());

        db.insert(NAME, null, values);
        db.close();
    }

    public void deleteOneFromList(String Title)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String DELETE_NAME = "DELETE FROM TitleTable WHERE title =" + "\"" + Title + "\"";
        db.execSQL(DELETE_NAME);
    }


    public void CreateSet(String name)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String CREATE_SET = "CREATE TABLE IF NOT EXISTS " + name + " (id INTEGER PRIMARY KEY AUTOINCREMENT, question TEXT, answer TEXT, image TEXT)";
        db.execSQL(CREATE_SET);
    }

    public void DeleteSet()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String SETNAME = RepeatsAddEditActivity.TITLE;
        String DELETE_SET = "DROP TABLE " + SETNAME;
        db.execSQL(DELETE_SET);
    }

    public void AddSet(RepeatsSingleSetDB Set)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_Q, Set.getQuestion());
        values.put(KEY_A, Set.getAnswer());
        values.put(KEY_I, Set.getImag());

        String SETNAME = RepeatsAddEditActivity.TITLE;
        db.insert(SETNAME, null, values);
        db.close();
    }

    public List<RepeatsSingleSetDB> AllItemsSET()
    {
        List<RepeatsSingleSetDB> ALL = new LinkedList<RepeatsSingleSetDB>();
        String SETNAME = RepeatsAddEditActivity.TITLE;
        String query = "SELECT * FROM " + SETNAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        RepeatsSingleSetDB list = null;

        if(cursor.moveToFirst())
        {
            do {
                list = new RepeatsSingleSetDB();
                list.setQuestion(cursor.getString(1));
                list.setAnswer(cursor.getString(2));
                list.setImag(cursor.getString(3));
                ALL.add(list);
            } while (cursor.moveToNext());
        }

        return ALL;
    }
}
