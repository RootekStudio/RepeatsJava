package com.rootekstudio.repeatsandroid.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.rootekstudio.repeatsandroid.fastlearning.FastLearningSetsListItem;
import com.rootekstudio.repeatsandroid.statistics.SetStats;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class RepeatsDatabase extends SQLiteOpenHelper {
    public RepeatsDatabase(Context context) {
        super(context, "repeats_database", null, 1);
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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void createSet(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        String CREATE_SET = "CREATE TABLE IF NOT EXISTS " + name + " " + "( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Values.question + " TEXT, " +
                Values.answer + " TEXT, " +
                Values.image + " TEXT, " +
                Values.good_answers + " INTEGER DEFAULT 0, " +
                Values.wrong_answers + " INTEGER DEFAULT 0);";
        db.execSQL(CREATE_SET);
        db.close();
    }

    public void addSetToSetsInfo(SingleSetInfo List) {
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
    }

    public void setSetName(String name, String SetID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Values.set_name, name);

        db.update(Values.sets_info, contentValues, Values.set_id + "=?", new String[]{SetID});
        db.close();
    }

    public void addEmptyItemToSet(String SetID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("question", "");
        values.put("answer", "");
        values.put("image", "");

        db.insert(SetID, null, values);
        db.close();
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

    public String getValueByCondition(String selectColumn, String table, String column , String value) {
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

    public void deleteSetFromSetInfo(String Title) {
        SQLiteDatabase db = this.getWritableDatabase();
        String DELETE_NAME = "DELETE FROM " + Values.sets_info + " WHERE " + Values.set_id + " =" + "'" + Title + "';";
        db.execSQL(DELETE_NAME);
        db.close();
    }

    public void deleteSet(String SETNAME) {
        SQLiteDatabase db = this.getWritableDatabase();
        String DELETE_SET = "DROP TABLE " + SETNAME + ";";
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
        String query = "INSERT INTO " + pasteSetID + " (" + Values.question + ", " + Values.answer +", " + Values.image + ")" +
                " SELECT " + Values.question + ", " + Values.answer + ", " + Values.image + " FROM " + copySetID + " WHERE " + Values.question + " != '' OR " + Values.answer + " != '';";
        db.execSQL(query);
        db.close();
    }

    public int itemsInSetCount(String setID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + setID;
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()) {
            return cursor.getInt(0);
        }

        cursor.close();
        db.close();
        return 0;
    }

    public List<String> getSingleQuestionAndAnswer(String SetID, int index) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<String> questionAndAnswer = null;

        String query = "SELECT " + Values.question + ", " + Values.answer + " FROM " + SetID +" WHERE id=" + index + ";";
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()) {
            questionAndAnswer = new ArrayList<>();
            do {
                questionAndAnswer.add(cursor.getString(0));
                questionAndAnswer.add(cursor.getString(1));
            } while(cursor.moveToNext());
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
            order = "ORDER BY id ASC";
        } else if (orderOption == Values.ORDER_BY_ID_DESC) {
            order = "ORDER BY id DESC";
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

    public int columnSum(String table, String column) {
        int sum = -1;
        String query = "SELECT SUM(" + column + ") FROM " + table + ";";
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

            SetSingleItem item = new SetSingleItem(setName);
            singleItems.add(item);

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
