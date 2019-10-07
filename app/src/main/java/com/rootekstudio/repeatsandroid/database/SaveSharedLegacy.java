package com.rootekstudio.repeatsandroid.database;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.rootekstudio.repeatsandroid.RepeatsListDB;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SaveSharedLegacy {
    static void saveSharedLegacy(Context context, DatabaseHelper DB) {
        final File dir = new File(context.getFilesDir(), "shared");
        File questions = new File(dir, "Questions.txt");
        File answers = new File(dir, "Answers.txt");

        SimpleDateFormat s = new SimpleDateFormat("yyyyMMddHHmmss");
        String date = s.format(new Date());

        SimpleDateFormat simpleDate = new SimpleDateFormat("dd.MM.yyyy");
        String createDate = simpleDate.format(new Date());

        String name = "";

        try{
            FileInputStream questionStream = new FileInputStream(questions);
            FileInputStream answerStream = new FileInputStream(answers);
            BufferedReader Qreader = new BufferedReader(new InputStreamReader(questionStream));
            BufferedReader Areader = new BufferedReader(new InputStreamReader(answerStream));
            String lineQ = Qreader.readLine();
            name = lineQ;
            SaveShared.name = name;
            String lineA = Areader.readLine();
            lineQ = Qreader.readLine();
            lineA = Areader.readLine();
            int i = 0;
            int itemIndex = 0;

            String id = "R" + date;
            SaveShared.ID = id;

            RepeatsListDB list = new RepeatsListDB(name, id, createDate, "true", "", "false");
            DB.CreateSet(id);
            DB.AddName(list);

            while (lineQ != null){

                final File image = new File(dir, "S" + i + ".png");
                if (image.exists()) {
                    String imageID = id.replace("R", "I");
                    imageID = imageID + itemIndex + ".png";

                    FileInputStream inputStream = new FileInputStream(image);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                    File control = new File(context.getFilesDir(), imageID);
                    boolean bool = control.createNewFile();

                    FileOutputStream out = new FileOutputStream(control);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);

                    DB.AddItemWithValues(id, lineQ, lineA, imageID);
                }
                else {
                    DB.AddItemWithValues(id, lineQ, lineA, "");
                }

                lineQ = Qreader.readLine();
                lineA = Areader.readLine();
                i++;

                itemIndex++;
            }
        }catch(IOException e){
            e.printStackTrace();
        }


    }
}
