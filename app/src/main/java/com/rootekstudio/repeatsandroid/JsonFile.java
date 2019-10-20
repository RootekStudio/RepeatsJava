package com.rootekstudio.repeatsandroid;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;

public class JsonFile {
    public static void createNewJson(Context context, String json, String fileName) {
        try{
            File jsonAdvanced = new File(context.getFilesDir(), fileName);
            FileWriter fileWriter = new FileWriter(jsonAdvanced);
            fileWriter.write(json);
            fileWriter.flush();
            fileWriter.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static String readJson(Context context, String fileName) {
        String json = "";
        try{
            File jsonAdvanced = new File(context.getFilesDir(), fileName);

            FileInputStream jsonStream = new FileInputStream(jsonAdvanced);
            BufferedReader jReader = new BufferedReader(new InputStreamReader(jsonStream));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = jReader.readLine()) != null) {
                sb.append(line);
            }
            json = sb.toString();

        }catch (Exception e) {
            e.printStackTrace();
        }

        return json;
    }
}
