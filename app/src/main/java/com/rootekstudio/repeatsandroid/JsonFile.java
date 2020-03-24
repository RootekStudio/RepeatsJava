package com.rootekstudio.repeatsandroid;

import android.content.Context;

import com.rootekstudio.repeatsandroid.database.DatabaseHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;

public class JsonFile {
    public static void createNewJson(Context context, String json, String fileName) {
        try {
            File jsonAdvanced = new File(context.getFilesDir(), fileName);
            FileWriter fileWriter = new FileWriter(jsonAdvanced);
            fileWriter.write(json);
            fileWriter.flush();
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String readJson(Context context, String fileName) {
        String json = "";
        try {
            File jsonAdvanced = new File(context.getFilesDir(), fileName);

            FileInputStream jsonStream = new FileInputStream(jsonAdvanced);
            BufferedReader jReader = new BufferedReader(new InputStreamReader(jsonStream));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = jReader.readLine()) != null) {
                sb.append(line);
            }
            json = sb.toString();

            jReader.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return json;
    }

    public static void removeSetFromJSON(Context context, String setIDToRemove) {
        try {
            JSONObject advancedFile = new JSONObject(readJson(context, "advancedDelivery.json"));
            Iterator<String> keys = advancedFile.keys();

            while (keys.hasNext()) {
                String index = keys.next();
                JSONObject single = advancedFile.getJSONObject(index);
                JSONArray sets = single.getJSONArray("sets");
                int itemCount = sets.length();

                for (int i = 0; i < itemCount; i++) {
                    String singleSet = sets.getString(i);
                    if (singleSet.equals(setIDToRemove)) {
                        sets.remove(i);
                        if (sets.length() == 0) {
                            DatabaseHelper DB = new DatabaseHelper(context);
                            ArrayList<String> setsID = DB.getSingleColumn("TableName");
                            if (setsID.size() > 0) {
                                sets.put(setsID.get(0));
                            }
                        }
                        break;
                    }
                }
            }

            createNewJson(context, advancedFile.toString(), "advancedDelivery.json");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void putSetToJSON(Context context, String setIDToPut) {
        try {
            JSONObject advancedFile = new JSONObject(readJson(context, "advancedDelivery.json"));
            Iterator<String> keys = advancedFile.keys();

            while (keys.hasNext()) {
                String index = keys.next();
                JSONObject single = advancedFile.getJSONObject(index);
                JSONArray sets = single.getJSONArray("sets");
                sets.put(setIDToPut);
            }

            createNewJson(context, advancedFile.toString(), "advancedDelivery.json");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<String> getSelectedSetsIdFromJSON(Context context, String jsonIndex) {
        ArrayList<String> sets = new ArrayList<>();
        try {
            JSONObject rootObject = new JSONObject(JsonFile.readJson(context, "advancedDelivery.json"));
            JSONObject singleCondition = rootObject.getJSONObject(jsonIndex);

            JSONArray setsArray = singleCondition.getJSONArray("sets");

            for (int i = 0; i < setsArray.length(); i++) {
                sets.add(setsArray.getString(i));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return sets;
    }
}
