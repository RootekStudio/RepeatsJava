package com.rootekstudio.repeatsandroid;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

class EditSetOperations
{
    static void SaveSetThread(final Context cnt,
                              final String name,
                              final Activity activity,
                              final List<Bitmap> bitmaps,
                              final List<String> ReadImages,
                              final String IgnoreChars,
                              final DatabaseHelper DB,
                              Boolean IsShare)
    {
        SimpleDateFormat s = new SimpleDateFormat("yyyyMMddHHmmss");
        final String SetName = "R" + s.format(new Date());
        final String SetImage = SetName.replace("R", "I");
        final String TITLE = SetName;

        SimpleDateFormat s1 = new SimpleDateFormat("dd.MM.yyyy");
        String CreateDate = s1.format(new Date());

        RepeatsListDB ListDB = new RepeatsListDB(name, SetName, CreateDate, "true", "", IgnoreChars);
        DB.AddName(ListDB);

        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                final ViewGroup par = activity.findViewById(R.id.AddRepeatsLinear);

                int itemscount = par.getChildCount();
                itemscount--;

                DB.CreateSet(SetName);

                String ImageName;
                int cImages = 0;
                int cBitmaps = 0;
                int cRead = 0;

                for (int i = 0; i <= itemscount; i++)
                {
                    View v = par.getChildAt(i);
                    EditText q = v.findViewById(R.id.questionBox);
                    EditText a = v.findViewById(R.id.answerBox);
                    ImageView img = v.findViewById(R.id.imageView);
                    String question = q.getText().toString();
                    String answer = a.getText().toString();
                    RepeatsSingleSetDB set;

                    if (img.getTag() != null)
                    {
                        ImageName = SetImage + cImages + ".png";

                        String TAG = img.getTag().toString();
                        if (TAG.equals("Y"))
                        {
                            Bitmap bitmap = bitmaps.get(cBitmaps);
                            try
                            {
                                File control = new File(cnt.getFilesDir(), ImageName);
                                boolean bool = control.createNewFile();

                                FileOutputStream out = new FileOutputStream(control);
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);

                            }
                            catch (IOException e)
                            {
                                e.printStackTrace();
                            }

                            cBitmaps++;
                        }
                        else
                            {
                            String filename = ReadImages.get(cRead);
                            File control = new File(cnt.getFilesDir(), filename);
                            boolean bool = control.renameTo(new File(cnt.getFilesDir(), ImageName));

                            cRead++;
                        }

                        set = new RepeatsSingleSetDB(question, answer, ImageName);
                        cImages++;
                    } else {
                        set = new RepeatsSingleSetDB(question, answer, "");
                    }

                    DB.AddSet(set, TITLE);
                }
            }
        });

        thread.start();

        if(IsShare)
        {
            try
            {
                thread.join();

            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }

            RepeatsAddEditActivity.TITLE = SetName;
        }

    }

    static void DeleteOldSet(String x, Context cnt, List<String> ImgToDelete)
    {
        DatabaseHelper DB = new DatabaseHelper(cnt);
        if (!x.equals("FALSE"))
        {
            DB.deleteOneFromList(x);
            DB.DeleteSet(x);
        }

        int delSize = ImgToDelete.size();

        if(delSize != 0)
        {
            for(int j = 0; j < delSize; j++)
            {
                String toDel = ImgToDelete.get(j);
                File file = new File(cnt.getFilesDir(), toDel);
                boolean del = file.delete();
            }
        }
    }

    static void DeleteSet(String x, Context cnt, List<String> ReadImages)
    {
        DatabaseHelper DB = new DatabaseHelper(cnt);
        if (!x.equals("FALSE"))
        {
            DB.deleteOneFromList(x);
            DB.DeleteSet(x);
        }

        int count = ReadImages.size();

        if(count != 0)
        {
            for(int j = 0; j < count; j++)
            {
                String imgName = ReadImages.get(j);
                File file = new File(cnt.getFilesDir(), imgName);
                boolean bool = file.delete();
            }
        }
    }
}
