package com.rootekstudio.repeatsandroid;

import android.content.Context;

import java.util.List;

public class Backup {
    void createBackup(Context context) {
        DatabaseHelper DB = new DatabaseHelper(context);
        List<RepeatsListDB> allSets = DB.AllItemsLIST();

        int setsCount = allSets.size();

        for(int i = 0; i < setsCount; i++) {
            RepeatsListDB single = allSets.get(i);

            String setID = single.getTableName();
            String name = single.getitle();

        }
    }

}
