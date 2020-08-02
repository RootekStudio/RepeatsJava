package com.rootekstudio.repeatsandroid.database;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.rootekstudio.repeatsandroid.SetsConfigHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class SaveSharedLegacy {
    private Context context;
    private RepeatsDatabase DB;

    String ID;
    public String setName;

    SaveSharedLegacy(Context context, RepeatsDatabase DB) {
        this.context = context;
        this.DB = DB;

        saveSharedLegacy();
    }
    private void saveSharedLegacy() {
        final File dir = new File(context.getFilesDir(), "shared");
        File questions = new File(dir, "Questions.txt");
        File answers = new File(dir, "Answers.txt");

        String name = "";

        try {
            FileInputStream questionStream = new FileInputStream(questions);
            FileInputStream answerStream = new FileInputStream(answers);
            BufferedReader Qreader = new BufferedReader(new InputStreamReader(questionStream));
            BufferedReader Areader = new BufferedReader(new InputStreamReader(answerStream));
            String lineQ = Qreader.readLine();
            name = lineQ;
            setName = name;
            String lineA = Areader.readLine();
            lineQ = Qreader.readLine();
            lineA = Areader.readLine();
            int i = 0;
            int itemIndex = 0;

            ID = new SetsConfigHelper(context).createNewSet(false, name);

            while (lineQ != null) {

                final File image = new File(dir, "S" + i + ".png");
                if (image.exists()) {
                    String imageID = "I" + ID;
                    imageID = imageID + itemIndex + ".png";

                    FileInputStream inputStream = new FileInputStream(image);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                    File control = new File(context.getFilesDir(), imageID);
                    boolean bool = control.createNewFile();

                    FileOutputStream out = new FileOutputStream(control);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);

                    DB.addItemToSetWithValues(ID, lineQ, lineA, imageID);
                } else {
                    DB.addItemToSetWithValues(ID, lineQ, lineA, "");
                }

                lineQ = Qreader.readLine();
                lineA = Areader.readLine();
                i++;

                itemIndex++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
