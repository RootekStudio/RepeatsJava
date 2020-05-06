package com.rootekstudio.repeatsandroid;

import android.content.Context;

import com.rootekstudio.repeatsandroid.database.RepeatsDatabase;
import com.rootekstudio.repeatsandroid.database.SetSingleItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

class SetToFile {
    static File zipFile;
    static List<String> filesToShare;
    static String fileName;

    static void saveSetsToFile(Context context, ArrayList<String> setsID, ArrayList<String> name) {
        RepeatsHelper.CheckDir(context);
        File directory = new File(context.getFilesDir(), "shared");
        File jsonFile = new File(directory, "sets.json");
        filesToShare = new ArrayList<>();
        filesToShare.add(jsonFile.getPath());

        JSONObject json = new JSONObject();

        try {
            JSONObject singleSet;
            RepeatsDatabase DB = new RepeatsDatabase(context);

            for (int set = 0; set < setsID.size(); set++) {
                singleSet = new JSONObject();
                List<SetSingleItem> list = DB.allItemsInSet(setsID.get(set), -1);
                int count = list.size();

                for (int i = 0; i < count; i++) {
                    SetSingleItem single = list.get(i);
                    String question = single.getQuestion();
                    String answer = single.getAnswer();
                    String image = single.getImage();

                    JSONObject singleItemJSON = new JSONObject();
                    singleItemJSON.put("question", question);

                    JSONArray answerArray = new JSONArray();
                    Scanner scanner = new Scanner(answer);
                    while (scanner.hasNextLine()) {
                        String singleAnswer = scanner.nextLine();
                        answerArray.put(singleAnswer);
                    }

                    singleItemJSON.put("answer", answerArray);


                    if (!image.equals("")) {
                        singleItemJSON.put("image", image);

                        File file = new File(context.getFilesDir(), image);
                        File copyImage = new File(directory, image);
                        copyFileUsingStream(file, copyImage);
                        filesToShare.add(copyImage.getPath());
                    }
                    singleSet.put(String.valueOf(i), singleItemJSON);
                }
                json.put(name.get(set), singleSet);
            }

            if (setsID.size() > 1) {
                SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
                String date = s.format(new Date());

                fileName = "backup-" + date + ".zip";
                zipFile = new File(directory, "backup-" + date + ".zip");
            } else {
                fileName = name.get(0) + ".zip";
                zipFile = new File(directory, name.get(0) + ".zip");
            }

            Boolean created = zipFile.createNewFile();
            Boolean set = zipFile.setWritable(true);

            FileWriter fileWriter = new FileWriter(jsonFile);
            String jsonSTRING = json.toString();
            fileWriter.append(jsonSTRING);
            fileWriter.flush();
            fileWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static void copyFileUsingStream(File source, File dest) {
        InputStream is;
        OutputStream os;
        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }

            is.close();
            os.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
