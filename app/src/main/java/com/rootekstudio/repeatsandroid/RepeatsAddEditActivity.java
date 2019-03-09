package com.rootekstudio.repeatsandroid;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.PreferenceManager;

public class RepeatsAddEditActivity extends AppCompatActivity
{
    public static String TITLE;
    private DatabaseHelper DB;
    private ViewGroup parent;
    ViewParent view;
    static Boolean IsDark;
    static Boolean IsTimeAsk = false;
    FragmentActivity activity;

    List<Bitmap> bitmaps = new ArrayList<>();
    List<String> ReadImages = new ArrayList<>();
    List<String> ImgToDelete = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        activity = this;

        IsDark = RepeatsHelper.DarkTheme(this);

        setContentView(R.layout.activity_repeats_add_edit);

        final Context cnt = this;
        DB = new DatabaseHelper(cnt);

        BottomAppBar bottomAppBar = findViewById(R.id.AddQuestionBar);
        bottomAppBar.replaceMenu(R.menu.bottomappbar_addset);

        parent = findViewById(R.id.AddRepeatsLinear);

        final LayoutInflater inflater = LayoutInflater.from(cnt);
        final Intent intent = new Intent(cnt, MainActivity.class);
        final EditText name = findViewById(R.id.projectname);

        Intent THISintent = getIntent();
        final String x = THISintent.getStringExtra("ISEDIT");
        final String n = THISintent.getStringExtra("NAME");

        //region FAB Action
        FloatingActionButton fab = findViewById(R.id.AddQuestionFAB);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ViewGroup parent = findViewById(R.id.AddRepeatsLinear);
                inflater.inflate(R.layout.addrepeatslistitem, parent);
                int items = parent.getChildCount();
                items--;
                View v2 = parent.getChildAt(items);
                ImageButton B = v2.findViewById(R.id.deleteItem);
                ImageButton I = v2.findViewById(R.id.addImage);

                if(IsDark)
                {
                    RelativeLayout RL = v2.findViewById(R.id.RelativeAddItem);
                    RL.setBackgroundResource(R.drawable.layout_mainshape_dark);
                }

                Delete_Button(B);
                Image_Button(I);
            }
        });
        //endregion

        //region Read set from Database
        if (!x.equals("FALSE"))
        {
            name.setText(n);
            TITLE = x;
            List<RepeatsSingleSetDB> SET = DB.AllItemsSET(TITLE);
            int ItemsCount = SET.size();

            for (int i = 0; i < ItemsCount; i++)
            {
                RepeatsSingleSetDB Single = SET.get(i);
                String Question = Single.getQuestion();
                String Answer = Single.getAnswer();
                String Image = Single.getImag();

                View view = inflater.inflate(R.layout.addrepeatslistitem, parent);
                View child = parent.getChildAt(i);

                if(IsDark)
                {
                    RelativeLayout RL = child.findViewById(R.id.RelativeAddItem);
                    RL.setBackgroundResource(R.drawable.layout_mainshape_dark);
                }

                EditText Q = child.findViewById(R.id.questionBox);
                EditText A = child.findViewById(R.id.answerBox);
                ImageButton B = child.findViewById(R.id.deleteItem);
                final ImageButton I = child.findViewById(R.id.addImage);
                ImageView img = child.findViewById(R.id.imageView);
                ImageButton imgbut = child.findViewById(R.id.deleteImage);

                Delete_Button(B);
                Image_Button(I);

                if(!Image.equals(""))
                {
                    I.setEnabled(false);
                    ReadImages.add(Image);

                    img.setVisibility(View.VISIBLE);
                    img.setTag(Image);
                    imgbut.setVisibility(View.VISIBLE);
                    try
                    {
                        File file = new File(getFilesDir(), Image);
                        FileInputStream inputStream = new FileInputStream(file);

                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        img.setImageBitmap(bitmap);
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }

                    imgbut.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            View pView = (View) v.getParent();
                            ImageView img = pView.findViewById(R.id.imageView);
                            ImageButton imgBut = pView.findViewById(R.id.deleteImage);
                            ImageButton imgAdd = pView.findViewById(R.id.addImage);
                            String tag = img.getTag().toString();
                            ImgToDelete.add(tag);
                            ReadImages.remove(tag);
                            img.setVisibility(View.GONE);
                            img.setTag(null);
                            imgBut.setVisibility(View.GONE);
                            imgAdd.setEnabled(true);
                            I.setEnabled(true);
                        }
                    });
                }
                Q.setText(Question);
                A.setText(Answer);
            }
        }
        //endregion
        //region If not editable, add new question
        else
            {
            inflater.inflate(R.layout.addrepeatslistitem, parent);
            View v = parent.getChildAt(0);
                if(IsDark)
                {
                    RelativeLayout RL = v.findViewById(R.id.RelativeAddItem);
                    RL.setBackgroundResource(R.drawable.layout_mainshape_dark);
                }
            final ImageButton deleteItem = v.findViewById(R.id.deleteItem);
            ImageButton I = v.findViewById(R.id.addImage);
            Delete_Button(deleteItem);
            Image_Button(I);
            }
            //endregion

        List<String> sth = ReadImages;
        int a = 0;

        bottomAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                //region Delete set
                if(item.getItemId() == R.id.deleteButton)
                {
                    AlertDialog.Builder ALERTbuilder = new AlertDialog.Builder(cnt);

                    ALERTbuilder.setMessage(R.string.WantDelete);
                    ALERTbuilder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {

                        }
                    });

                    ALERTbuilder.setPositiveButton(R.string.Delete, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            if(!x.equals("FALSE"))
                            {
                                DB.deleteOneFromList(x);
                                DB.DeleteSet(x);

                                List<RepeatsListDB> a = DB.AllItemsLIST();
                                int size = a.size();

                                if(size == 0)
                                {
                                    RepeatsHelper.CancelNotifications(cnt);
                                }

                                int count = ReadImages.size();

                                if(count != 0)
                                {
                                    for(int j = 0; j < count; j++)
                                    {
                                        String imgName = ReadImages.get(j);
                                        File file = new File(getFilesDir(), imgName);
                                        boolean bool = file.delete();
                                    }
                                }
                            }
                            RepeatsAddEditActivity.super.onBackPressed();
                        }
                    });
                    ALERTbuilder.show();


                }
                //endregion
                //region Save set
                else if(item.getItemId() == R.id.saveButton)
                {
                    final ProgressBar progressBar = findViewById(R.id.ProgressBar);
                    progressBar.setVisibility(View.VISIBLE);


                    Thread thread = new Thread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            progressBar.setVisibility(View.VISIBLE);

                            final ViewGroup par = findViewById(R.id.AddRepeatsLinear);
                            int itemscount = par.getChildCount();
                            itemscount--;

                            SimpleDateFormat s = new SimpleDateFormat("yyyyMMddHHmmss");
                            String SetName = "R" + s.format(new Date());
                            String SetImage = SetName.replace("R", "I");
                            TITLE = SetName;

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

                                if(img.getTag() != null)
                                {
                                    ImageName = SetImage + cImages + ".png";

                                    String TAG = img.getTag().toString();
                                    if(TAG.equals("Y"))
                                    {
                                        Bitmap bitmap = bitmaps.get(cBitmaps);
                                        try
                                        {
                                            File control = new File(cnt.getFilesDir(), ImageName);
                                            boolean bool = control.createNewFile();

                                            FileOutputStream out = new FileOutputStream(control);
                                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);

                                        } catch (IOException e)
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
                                }
                                else
                                {
                                    set = new RepeatsSingleSetDB(question, answer, "");
                                }

                                DB.AddSet(set, TITLE);
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

                            String TableName = name.getText().toString();
                            SimpleDateFormat s1 = new SimpleDateFormat("dd.MM.yyyy");
                            String CreateDate = s1.format(new Date());

                            RepeatsListDB ListDB = new RepeatsListDB(TableName, SetName, CreateDate, "true", "test");
                            DB.AddName(ListDB);

                            if (!x.equals("FALSE"))
                            {
                                TITLE = x;
                                DB.deleteOneFromList(x);
                                DB.DeleteSet(x);
                            }
                        }
                    });
                    thread.start();
                    try
                    {
                        thread.join();
                    } catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }

                    progressBar.setVisibility(View.GONE);
                    final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(cnt);
                    int freq = sharedPreferences.getInt("frequency", 0);
                    if(freq == 0)
                    {
                        RepeatsHelper.AskAboutTime(cnt, true, activity);
                        IsTimeAsk = true;
                    }
                    else
                    {
                        RepeatsAddEditActivity.super.onBackPressed();
                    }

                }
                //endregion

                return true;
            }
        });
    }

    //region Delete Single Question
    private void Delete_Button(ImageButton button)
    {
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(parent.getChildCount() > 1)
                {
                    View view = (View)v.getParent();
                    int a = parent.indexOfChild(view);
                    ImageView imgView = view.findViewById(R.id.imageView);

                    if(imgView.getVisibility() == View.VISIBLE)
                    {
                        String TAG = imgView.getTag().toString();
                        ImgToDelete.add(TAG);
                        ReadImages.remove(TAG);
                    }

                    parent.removeViewAt(a);
                }
            }
        });
    }
    //endregion

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == 1)
        {
            if (resultCode == RESULT_OK)
            {
                Uri selectedImage = data.getData();
                final InputStream imageStream;
                try
                {
                    RelativeLayout rel = (RelativeLayout) view;
                    final ImageView imageView = rel.findViewById(R.id.imageView);

                    imageView.setVisibility(View.VISIBLE);
                    imageStream = getContentResolver().openInputStream(selectedImage);

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeStream(imageStream, null, options);
                    options.inSampleSize = calculateInSampleSize(options, 500, 500);
                    options.inJustDecodeBounds = false;

                    InputStream is = getContentResolver().openInputStream(selectedImage);
                    final Bitmap selected = BitmapFactory.decodeStream(is, null, options);

                    bitmaps.add(selected);
                    imageView.setImageBitmap(selected);

                    SimpleDateFormat s = new SimpleDateFormat("yyyyMMddHHmmss");

                    imageView.setTag("Y");
                    final ImageButton imgbut = rel.findViewById(R.id.deleteImage);
                    final ImageButton addimg = rel.findViewById(R.id.addImage);
                    addimg.setEnabled(false);

                    imgbut.setVisibility(View.VISIBLE);
                    imgbut.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            imageView.setVisibility(View.GONE);
                            imageView.setImageBitmap(null);
                            imageView.setTag(null);
                            imgbut.setVisibility(View.GONE);
                            addimg.setEnabled(true);

                        }
                    });

                }
                catch (FileNotFoundException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    private void Image_Button(ImageButton button)
    {
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                view = v.getParent();

                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(photoPickerIntent, 1);
            }
        });
    }


    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {

        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    @Override
    public void onBackPressed()
    {
        if(!IsTimeAsk)
        {
            new AlertDialog.Builder(this)
                    .setMessage(R.string.WantLeave)
                    .setNegativeButton(R.string.Cancel, null)
                    .setPositiveButton(R.string.Leave, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            RepeatsAddEditActivity.super.onBackPressed();
                        }
                    }).create().show();

            IsTimeAsk = false;
        }
        else
        {
            RepeatsAddEditActivity.super.onBackPressed();
        }

    }
}
