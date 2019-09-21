package com.rootekstudio.repeatsandroid;
import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

class SetsMigrationTool
{
    static void MigrateFromOldVersion(Context context)
    {
        try
        {
            FileInputStream stream = context.openFileInput("ProjectsName.txt");
            InputStreamReader streamReader = new InputStreamReader(stream);

            BufferedReader reader = new BufferedReader(streamReader);

            int cnt = 0;
            String name;
            while ((name = reader.readLine()) != null)
            {
                cnt++;
                SimpleDateFormat s = new SimpleDateFormat("yyyyMMddHHmmss");
                String SetName = "R" + cnt + s.format(new Date());

                DatabaseHelper DB = new DatabaseHelper(context);
                DB.CreateSet(SetName);

                FileInputStream SetStream = context.openFileInput(name + ".txt");
                InputStreamReader SetReader = new InputStreamReader(SetStream);

                BufferedReader readSet = new BufferedReader(SetReader);

                String Questions = readSet.readLine();
                int count = Integer.parseInt(Questions);

                for(int i = 1; i <= count; i++)
                {
                    String readQ = readSet.readLine();
                    String readA = readSet.readLine();
                    readQ = readQ.replace("r" + i +"Q: ", "");
                    readA = readA.replace("r" + i +"A: ", "");

//                    RepeatsSingleSetDB Single = new RepeatsSingleSetDB(readQ, readA, "");
//                    DB.AddSet(Single, SetName);
                }

                SimpleDateFormat s1 = new SimpleDateFormat("dd.MM.yyyy");
                String CreateDate = s1.format(new Date());

                RepeatsListDB ListDB = new RepeatsListDB(name, SetName, CreateDate, "true", "", "false");
                DB.AddName(ListDB);
            }

            File control = new File(context.getFilesDir(), "SetsMigrationCompleted.txt");
            boolean bool = control.createNewFile();

        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
