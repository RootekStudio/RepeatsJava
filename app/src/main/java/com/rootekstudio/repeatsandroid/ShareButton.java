package com.rootekstudio.repeatsandroid;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.EditText;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import androidx.core.content.FileProvider;

public class ShareButton
{
    static void ShareClick(Context context, EditText name, String x, String TITLE)
    {
        File directory = new File(context.getFilesDir(), "shared");

        if(!directory.exists())
        {
            Boolean c = directory.mkdir();
        }
        else
        {
            String[] files = directory.list();
            int count = files.length;
            if(count != 0)
            {
                for (String file : files)
                {
                    File toDel = new File(directory, file);
                    Boolean delete = toDel.delete();
                }
            }
        }

        //SaveSetThread(context, name, x);

        File questions = new File(directory, "Questions.txt");
        File answers = new File(directory, "Answers.txt");
        List<String> filesToShare = new ArrayList<>();
        filesToShare.add(questions.getPath());
        filesToShare.add(answers.getPath());

        try
        {
            questions.createNewFile();
            answers.createNewFile();

            FileWriter Qwriter = new FileWriter(questions);
            FileWriter Awriter = new FileWriter(answers);

            DatabaseHelper DB = new DatabaseHelper(context);
            List<RepeatsSingleSetDB> list = DB.AllItemsSET(TITLE);
            int count = list.size();

            String NAME = name.getText().toString();
            Qwriter.append(NAME);
            Qwriter.append(System.getProperty("line.separator"));
            Awriter.append(NAME);
            Awriter.append(System.getProperty("line.separator"));

            Qwriter.flush();
            Awriter.flush();

            for(int i = 0; i < count; i++)
            {
                RepeatsSingleSetDB single = list.get(i);
                Qwriter.append(single.getQuestion());
                Qwriter.append(System.getProperty("line.separator"));
                Awriter.append(single.getAnswer());
                Awriter.append(System.getProperty("line.separator"));

                String image = single.getImag();

                if(!image.equals(""))
                {
                    File file = new File(context.getFilesDir(), image);
                    File copyImage = new File(directory, "S" + Integer.toString(i) + ".png");
                    copyFileUsingStream(file, copyImage);
                    filesToShare.add(copyImage.getPath());
                }

                Qwriter.flush();
                Awriter.flush();
            }

            Qwriter.close();
            Awriter.close();

            File zipFile = new File(directory, NAME + ".zip");
            Boolean created = zipFile.createNewFile();
            Boolean set = zipFile.setWritable(true);

            ShareSet.zip(filesToShare, zipFile);

            Boolean check = zipFile.exists();

            Uri uri = FileProvider.getUriForFile(context, "com.rootekstudio.repeatsandroid.RepeatsAddEditActivity", zipFile);

            Intent share = new Intent();
            share.setAction(Intent.ACTION_SEND);
            share.putExtra(Intent.EXTRA_STREAM, uri);
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            share.setType("application/zip");
            context.startActivity(Intent.createChooser(share,"Test"));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static void copyFileUsingStream(File source, File dest)
    {
        InputStream is;
        OutputStream os;
        try
        {
            is = new FileInputStream(source);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0)
            {
                os.write(buffer, 0, length);
            }

            is.close();
            os.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
