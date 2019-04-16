package com.rootekstudio.repeatsandroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
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
import android.widget.RelativeLayout;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.PreferenceManager;

public class RepeatsAddEditActivity extends AppCompatActivity
{
    public static String TITLE;
    public String IgnoreChars = "false";
    DatabaseHelper DB;
    ViewGroup parent;
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

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Context cnt = this;
        DB = new DatabaseHelper(cnt);

        BottomAppBar bottomAppBar = findViewById(R.id.AddQuestionBar);
        bottomAppBar.replaceMenu(R.menu.bottomappbar_addset);

        final Activity repeatsAddEditActivity = this;

        parent = findViewById(R.id.AddRepeatsLinear);

        final LayoutInflater inflater = LayoutInflater.from(cnt);
        final EditText name = findViewById(R.id.projectname);

        Intent THISintent = getIntent();
        final String ISEDIT = THISintent.getStringExtra("ISEDIT");
        final String ignore = THISintent.getStringExtra("IGNORE_CHARS");
        final boolean shared = THISintent.getBooleanExtra("LoadShared", false);

        if(ignore.equals("true"))
        {
            IgnoreChars = "true";
            bottomAppBar.getMenu().findItem(R.id.ignoreCharsItem).setChecked(true);
        }

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
        if (!ISEDIT.equals("FALSE"))
        {
            String n = THISintent.getStringExtra("NAME");
            name.setText(n);
            TITLE = ISEDIT;
            List<RepeatsSingleSetDB> SET = DB.AllItemsSET(TITLE);
            int ItemsCount = SET.size();

            for (int i = 0; i < ItemsCount; i++)
            {
                RepeatsSingleSetDB Single = SET.get(i);
                String Question = Single.getQuestion();
                String Answer = Single.getAnswer();
                String Image = Single.getImag();

                inflater.inflate(R.layout.addrepeatslistitem, parent);
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
                            DeleteImage_Button(v, I);
                        }
                    });
                }
                Q.setText(Question);
                A.setText(Answer);
            }
        }
        //endregion
        //region Read From Zip
        else if (shared)
        {
            File dir = new File(getFilesDir(), "shared");
            File questions = new File(dir, "Questions.txt");
            Boolean q = questions.exists();
            File answers = new File(dir, "Answers.txt");
            Boolean a = answers.exists();
            try
            {
                FileInputStream questionStream = new FileInputStream(questions);
                FileInputStream answerStream = new FileInputStream(answers);
                BufferedReader Qreader = new BufferedReader(new InputStreamReader(questionStream));
                BufferedReader Areader = new BufferedReader(new InputStreamReader(answerStream));
                String lineQ = Qreader.readLine();
                name.setText(lineQ);
                TITLE = lineQ;
                String lineA = Areader.readLine();
                lineQ = Qreader.readLine();
                lineA = Areader.readLine();
                int i = 0;
                int int_image = 0;
                while (lineQ != null)
                {
                    inflater.inflate(R.layout.addrepeatslistitem, parent);
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

                    File image = new File(dir, "S" + i + ".png");
                    if (image.exists())
                    {
                        I.setEnabled(false);

                        img.setVisibility(View.VISIBLE);
                        img.setTag("Y");

                        imgbut.setVisibility(View.VISIBLE);
                        imgbut.setTag(int_image);
                        int_image++;
                        try
                        {
                            FileInputStream inputStream = new FileInputStream(image);

                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            img.setImageBitmap(bitmap);
                            bitmaps.add(bitmap);
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
                                DeleteImage_Button(v, I);
                            }
                        });
                    }

                    Q.setText(lineQ);
                    A.setText(lineA);

                    lineQ = Qreader.readLine();
                    lineA = Areader.readLine();
                    i++;
                }

                Boolean delQ = questions.delete();
                Boolean delA = answers.delete();
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
        //endregion
        //region If not editable, add new question
        else
            {
            inflater.inflate(R.layout.addrepeatslistitem, parent);
            View v = parent.getChildAt(0);

                if (IsDark)
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
                    ALERTbuilder.setNegativeButton(R.string.Cancel, null);

                    ALERTbuilder.setPositiveButton(R.string.Delete, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            if(!ISEDIT.equals("FALSE"))
                            {
                                EditSetOperations.DeleteOldSet(ISEDIT, cnt, ImgToDelete);

                                List<RepeatsListDB> a = DB.AllItemsLIST();
                                int size = a.size();

                                if(size == 0)
                                {
                                    RepeatsHelper.CancelNotifications(cnt);
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


                    String TableName = name.getText().toString();

                    EditSetOperations.SaveSetThread(cnt, TableName, repeatsAddEditActivity, bitmaps, ReadImages, IgnoreChars, DB);
                    EditSetOperations.DeleteOldSet(ISEDIT, cnt, ImgToDelete);

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
                //region Ignore special characters
                if(item.getItemId() == R.id.ignoreCharsItem)
                {
                    if(item.isChecked())
                    {
                        IgnoreChars = "false";
                        item.setChecked(false);
                    }
                    else
                    {
                        IgnoreChars = "true";
                        item.setChecked(true);
                    }
                }
                //endregion
                //region Share button
                if(item.getItemId() == R.id.share)
                {
                    String TableName = name.getText().toString();

                    EditSetOperations.SaveSetThread(cnt, TableName, repeatsAddEditActivity, bitmaps, ReadImages, IgnoreChars, DB);
                    EditSetOperations.DeleteOldSet(ISEDIT, cnt, ImgToDelete);

                    ShareButton.ShareClick(cnt, TableName, TITLE);

                    RepeatsAddEditActivity.super.onBackPressed();
                }
                //endregion

                return true;
            }
        });
    }

    private void DeleteImage_Button(View v, ImageButton I)
    {
        View pView = (View) v.getParent();
        ImageView img = pView.findViewById(R.id.imageView);
        ImageButton imgBut = pView.findViewById(R.id.deleteImage);
        ImageButton imgAdd = pView.findViewById(R.id.addImage);
        String tag = img.getTag().toString();
        int rem = Integer.parseInt(imgBut.getTag().toString());
        ImgToDelete.add(tag);
        ReadImages.remove(tag);
        bitmaps.remove(rem);
        img.setVisibility(View.GONE);
        img.setTag(null);
        imgBut.setVisibility(View.GONE);
        imgAdd.setEnabled(true);
        I.setEnabled(true);
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
                    options.inSampleSize = RepeatsHelper.calculateInSampleSize(options, 500, 500);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == android.R.id.home)
        {
            onBackPressed();
        }
        return true;
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
