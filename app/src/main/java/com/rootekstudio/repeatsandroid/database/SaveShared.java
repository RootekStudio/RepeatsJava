package com.rootekstudio.repeatsandroid.database;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.rootekstudio.repeatsandroid.JsonFile;
import com.rootekstudio.repeatsandroid.RepeatsHelper;
import com.rootekstudio.repeatsandroid.RepeatsListDB;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

public class SaveShared {
    public static String ID;
    public static String name;

    public static void SaveSetsToDB(Context context, DatabaseHelper DB) {

        File dir = new File(context.getFilesDir(), "shared");

        File legacyFile = new File(dir, "Answers.txt");
        if(legacyFile.exists()) {
            SaveSharedLegacy.saveSharedLegacy(context, DB);
            return;
        }

        File jsonFile = new File(dir, "sets.json");

        SimpleDateFormat s = new SimpleDateFormat("yyyyMMddHHmmss");
        String date = s.format(new Date());

        SimpleDateFormat simpleDate = new SimpleDateFormat("dd.MM.yyyy");
        String createDate = simpleDate.format(new Date());

        try {

            FileInputStream jsonStream = new FileInputStream(jsonFile);
            BufferedReader jReader = new BufferedReader(new InputStreamReader(jsonStream));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = jReader.readLine()) != null) {
                sb.append(line);
            }

            String fullJSON = sb.toString();

            JSONObject rootObject = new JSONObject(fullJSON);
            Iterator<String> iterator = rootObject.keys();
            ArrayList<String> keys = new ArrayList<>();

            int setIndex = 0;
            int itemIndex = 0;

            do {
                keys.add(iterator.next());
            } while (iterator.hasNext());

            do {
                String id = "R" + date + setIndex;
                ID = id;

                JSONObject singleSet = rootObject.getJSONObject(keys.get(setIndex));

                name = keys.get(setIndex);

                RepeatsListDB list;
                if(Locale.getDefault().toString().equals("pl_PL")) {
                    list = new RepeatsListDB(name, id, createDate, "true", "", "false", "pl_PL", "en_GB");
                }
                else {
                    list = new RepeatsListDB(name, id, createDate, "true", "", "false", "en_US", "es_ES");
                }

                DB.CreateSet(id);
                DB.AddName(list);

                JsonFile.putSetToJSON(context, id);

                do {
                    JSONObject singleItem = singleSet.getJSONObject(String.valueOf(itemIndex));
                    String question = singleItem.getString("question");
                    String answer;
                    String image = "";
                    JSONArray answerArray = singleItem.getJSONArray("answer");
                    int answerLength = answerArray.length();

                    if (answerLength == 1) {
                        answer = answerArray.getString(0);
                    } else {
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int j = 0; j < answerLength; j++) {
                            stringBuilder.append(answerArray.getString(j));
                            if(j != answerLength-1) {
                                stringBuilder.append(RepeatsHelper.breakLine);
                            }
                        }
                        answer = stringBuilder.toString();
                    }

                    if (!singleItem.isNull("image")) {
                        image = singleItem.getString("image");

                        File imageDir = new File(dir, image);

                        String imageID = id.replace("R", "I");
                        imageID = imageID + itemIndex + ".png";

                        FileInputStream inputStream = new FileInputStream(imageDir);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                        File control = new File(context.getFilesDir(), imageID);
                        boolean bool = control.createNewFile();

                        FileOutputStream out = new FileOutputStream(control);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);

                        DB.AddItemWithValues(id, question, answer, imageID);
                    }
                    else {
                        DB.AddItemWithValues(id, question, answer, "");
                    }

                    itemIndex++;

                } while (!singleSet.isNull(String.valueOf(itemIndex)));

                setIndex++;

                itemIndex = 0;

                if (setIndex == keys.size()) {
                    break;
                }

            } while (!rootObject.isNull(keys.get(setIndex)));

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
