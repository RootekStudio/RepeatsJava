package com.rootekstudio.repeatsandroid;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

class SaveShared {

    static String ID;
    static String name;

    static void SaveSharedToDB(Context context, DatabaseHelper DB) {

        File dir = new File(context.getFilesDir(), "shared");
        File questions = new File(dir, "Questions.txt");
        File answers = new File(dir, "Answers.txt");

        SimpleDateFormat s = new SimpleDateFormat("yyyyMMddHHmmss");
        String date = s.format(new Date());
        String id = "R" + date;
        ID = id;

        SimpleDateFormat simpleDate = new SimpleDateFormat("dd.MM.yyyy");
        String createDate = simpleDate.format(new Date());

        try {
            FileInputStream questionStream = new FileInputStream(questions);
            FileInputStream answerStream = new FileInputStream(answers);
            BufferedReader Qreader = new BufferedReader(new InputStreamReader(questionStream));
            BufferedReader Areader = new BufferedReader(new InputStreamReader(answerStream));

            String lineQ = Qreader.readLine();
            String lineA = Areader.readLine();

            name = lineQ;

            RepeatsListDB list = new RepeatsListDB(lineQ, id, createDate, "true", "", "false");

            DB.CreateSet(id);
            DB.AddName(list);

            int i = 0;

            while (lineQ != null) {

                File image = new File(dir, "S" + i + ".png");
                if(image.exists()) {
                    try
                    {
                        String imageID = id.replace("R", "I");
                        imageID = imageID + i + ".png";

                        FileInputStream inputStream = new FileInputStream(image);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                        File control = new File(context.getFilesDir(),imageID);
                        boolean bool = control.createNewFile();

                        FileOutputStream out = new FileOutputStream(control);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);

                        DB.AddItemWithValues(id, lineQ, lineA, imageID);
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();

                        DB.AddItemWithValues(id, lineQ, lineA, "");
                    }
                }
                else {
                    DB.AddItemWithValues(id, lineQ, lineA, "");
                }

                lineQ = Qreader.readLine();
                lineA = Areader.readLine();
                i++;
            }

        } catch(FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}
