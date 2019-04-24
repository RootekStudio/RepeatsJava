package com.rootekstudio.repeatsandroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

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

class ShareButton
{
    static void ShareClick(final Context context, final String name, final String TITLE, final Activity activity)
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final LayoutInflater layoutInflater = LayoutInflater.from(context);
        final View view1 = layoutInflater.inflate(R.layout.progress, null);

        builder.setView(view1);
        builder.setMessage(R.string.loading);
        builder.setCancelable(false);

        TextView textView = view1.findViewById(R.id.textProgress);
        textView.setVisibility(View.GONE);
        ProgressBar progressBar = view1.findViewById(R.id.progressBar);
        progressBar.setIndeterminate(true);

        final AlertDialog dialog = builder.create();
        activity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                dialog.show();
            }
        });


        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                File directory = new File(context.getFilesDir(), "shared");

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

                    Qwriter.append(name);
                    Qwriter.append(System.getProperty("line.separator"));
                    Awriter.append(name);
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
                            File copyImage = new File(directory, "S" + i + ".png");
                            copyFileUsingStream(file, copyImage);
                            filesToShare.add(copyImage.getPath());
                        }

                        Qwriter.flush();
                        Awriter.flush();
                    }

                    Qwriter.close();
                    Awriter.close();

                    File zipFile = new File(directory, name + ".zip");
                    Boolean created = zipFile.createNewFile();
                    Boolean set = zipFile.setWritable(true);

                    ZipSet.zip(filesToShare, zipFile);

                    Boolean check = zipFile.exists();

                    Uri uri = FileProvider.getUriForFile(context, "com.rootekstudio.repeatsandroid.RepeatsAddEditActivity", zipFile);

                    Intent share = new Intent();
                    share.setAction(Intent.ACTION_SEND);
                    share.putExtra(Intent.EXTRA_STREAM, uri);
                    share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    share.setType("application/zip");
                    activity.runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            dialog.dismiss();
                        }
                    });
                    activity.startActivityForResult(Intent.createChooser(share, context.getString(R.string.share)), 111);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
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
