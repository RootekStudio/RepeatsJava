package com.rootekstudio.repeatsandroid;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SetsMigrationTool
{
    static void MigrateFromOldVersion(Context context)
    {
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader("ProjectsName.txt"));
            File control = new File(context.getFilesDir(), "ProjectsNameM.txt");

            String name;
            while ((name = reader.readLine()) != null)
            {
                SimpleDateFormat s = new SimpleDateFormat("yyyyMMddHHmmss");
                String SetName = "R" + s.format(new Date());

                DatabaseHelper DB = new DatabaseHelper(context);
                DB.CreateSet(SetName);

                BufferedReader readSet = new BufferedReader(new FileReader(name + ".txt"));
                String Questions = readSet.readLine();
                int count = Integer.parseInt(Questions);

                for(int i = 0; i < count; i++)
                {
                    String readQ = readSet.readLine();
                    String readA = readSet.readLine();
                    readQ = readQ.replace("r1Q: ", "");
                    readA = readA.replace("r1A: ", "");

                    RepeatsSingleSetDB Single = new RepeatsSingleSetDB(readQ, readA, "");
                    DB.AddSet(Single, SetName);
                }

                SimpleDateFormat s1 = new SimpleDateFormat("dd.MM.yyyy");
                String CreateDate = s1.format(new Date());

                RepeatsListDB ListDB = new RepeatsListDB(SetName, name, CreateDate, "true", "");
                DB.AddName(ListDB);
            }

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
