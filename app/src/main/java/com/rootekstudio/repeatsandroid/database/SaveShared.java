package com.rootekstudio.repeatsandroid.database;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.rootekstudio.repeatsandroid.RepeatsHelper;
import com.rootekstudio.repeatsandroid.SetsConfigHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;

public class SaveShared {
    public static String ID;
    public static String name;

    public static void SaveSetsToDB(Context context, RepeatsDatabase DB) {

        File dir = new File(context.getFilesDir(), "shared");

        File legacyFile = new File(dir, "Answers.txt");
        if (legacyFile.exists()) {
            SaveSharedLegacy saveSharedLegacy = new SaveSharedLegacy(context, DB);
            ID = saveSharedLegacy.ID;
            name = saveSharedLegacy.setName;
            return;
        }

        File jsonFile = new File(dir, "sets.json");
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
                JSONObject singleSet = rootObject.getJSONObject(keys.get(setIndex));
                name = keys.get(setIndex);
                ID = new SetsConfigHelper(context).createNewSet(false, name);
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
                            if (j != answerLength - 1) {
                                stringBuilder.append(RepeatsHelper.breakLine);
                            }
                        }
                        answer = stringBuilder.toString();
                    }

                    if (!singleItem.isNull("image")) {
                        image = singleItem.getString("image");

                        File imageDir = new File(dir, image);

                        String imageID = "I" + ID;
                        imageID = imageID + itemIndex + ".png";

                        FileInputStream inputStream = new FileInputStream(imageDir);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                        File control = new File(context.getFilesDir(), imageID);
                        boolean bool = control.createNewFile();

                        FileOutputStream out = new FileOutputStream(control);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);

                        DB.addItemToSetWithValues(ID, question, answer, imageID);
                    } else {
                        DB.addItemToSetWithValues(ID, question, answer, "");
                    }

                    itemIndex++;

                } while (!singleSet.isNull(String.valueOf(itemIndex)));

                setIndex++;

                itemIndex = 0;

                if (setIndex == keys.size()) {
                    break;
                }

            } while (!rootObject.isNull(keys.get(setIndex)));

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }
}
