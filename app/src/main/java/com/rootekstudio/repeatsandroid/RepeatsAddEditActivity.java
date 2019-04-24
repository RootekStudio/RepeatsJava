package com.rootekstudio.repeatsandroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

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

public class RepeatsAddEditActivity extends AppCompatActivity
{
    public String IgnoreChars;
    DatabaseHelper DB;
    ViewGroup parent;
    ViewParent view;
    static Boolean IsDark;
    static Boolean IsTimeAsk;
    static String ISEDIT;
    FragmentActivity activity;
    static String NewName;
    static int element;

    List<Bitmap> bitmaps = new ArrayList<>();
    List<String> ReadImages = new ArrayList<>();
    List<String> ImgToDelete = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        element = -1;
        IgnoreChars = "false";
        IsTimeAsk = false;

        super.onCreate(savedInstanceState);
        activity = this;

        IsDark = RepeatsHelper.DarkTheme(this);

        setContentView(R.layout.activity_repeats_add_edit);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Context cnt = this;
        DB = new DatabaseHelper(cnt);

        BottomAppBar bottomAppBar = findViewById(R.id.AddQuestionBar);
        bottomAppBar.replaceMenu(R.menu.bottomappbar_addset);

        if(!IsDark)
        {
            bottomAppBar.setBackgroundTint(ContextCompat.getColorStateList(this, R.color.DayColorPrimaryDark));
        }

        final Activity repeatsAddEditActivity = this;

        parent = findViewById(R.id.AddRepeatsLinear);

        final LayoutInflater inflater = LayoutInflater.from(cnt);
        final EditText name = findViewById(R.id.projectname);

        final Intent THISintent = getIntent();
        ISEDIT = THISintent.getStringExtra("ISEDIT");
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

                element++;
                RelativeLayout RL = v2.findViewById(R.id.RelativeAddItem);
                RL.setTag(element);

                if(IsDark)
                {
                    RL.setBackgroundResource(R.drawable.layout_mainshape_dark);
                }

                bitmaps.add(null);
                Delete_Button(B);
                Image_Button(I);
            }
        });
        //endregion

        //region Read set from Database
        if (!ISEDIT.equals("FALSE"))
        {
            Thread readFromDatabase = new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    String n = THISintent.getStringExtra("NAME");
                    name.setText(n);
                    List<RepeatsSingleSetDB> SET = DB.AllItemsSET(ISEDIT);
                    int ItemsCount = SET.size();

                    for (int i = 0; i < ItemsCount; i++)
                    {
                        RepeatsSingleSetDB Single = SET.get(i);
                        String Question = Single.getQuestion();
                        String Answer = Single.getAnswer();
                        String Image = Single.getImag();

                        final View child = inflater.inflate(R.layout.addrepeatslistitem, parent, false);

                        element++;
                        RelativeLayout RL = child.findViewById(R.id.RelativeAddItem);
                        RL.setTag(element);

                        if(IsDark)
                        {
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

                        bitmaps.add(null);

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

                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                parent.addView(child);
                            }
                        });
                    }
                }
            });
            readFromDatabase.start();
        }
        //endregion
        //region Read From Zip
        else if (shared)
        {
            Thread readFromZip = new Thread(new Runnable()
            {
                @Override
                public void run()
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
                        String lineA = Areader.readLine();
                        lineQ = Qreader.readLine();
                        lineA = Areader.readLine();
                        int i = 0;
                        int int_image = 0;
                        while (lineQ != null)
                        {
                            final RelativeLayout child = (RelativeLayout) inflater.inflate(R.layout.addrepeatslistitem, parent, false);

                            element++;
                            RelativeLayout RL = child.findViewById(R.id.RelativeAddItem);
                            RL.setTag(element);

                            if(IsDark)
                            {
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

                            bitmaps.add(null);

                            final File image = new File(dir, "S" + i + ".png");
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
                                    bitmaps.set(element, bitmap);
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
                                        ImageView imageView = pView.findViewById(R.id.imageView);
                                        ImageButton imgbut = pView.findViewById(R.id.deleteImage);
                                        ImageButton addimg = pView.findViewById(R.id.addImage);

                                        int e = Integer.parseInt(pView.getTag().toString());
                                        bitmaps.set(e, null);

                                        imageView.setVisibility(View.GONE);
                                        imageView.setImageBitmap(null);
                                        imageView.setTag(null);
                                        imgbut.setVisibility(View.GONE);
                                        addimg.setEnabled(true);
                                    }
                                });
                            }

                            Q.setText(lineQ);
                            A.setText(lineA);

                            lineQ = Qreader.readLine();
                            lineA = Areader.readLine();
                            i++;

                            runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    parent.addView(child);
                                }
                            });
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
            });

            readFromZip.start();
        }
        //endregion
        //region If not editable, add new question
        else
            {
            inflater.inflate(R.layout.addrepeatslistitem, parent);
            element++;
            View v = parent.getChildAt(0);
            RelativeLayout RL = v.findViewById(R.id.RelativeAddItem);
            RL.setTag(element);

            if (IsDark)
            {
                RL.setBackgroundResource(R.drawable.layout_mainshape_dark);
            }

            final ImageButton deleteItem = v.findViewById(R.id.deleteItem);
            ImageButton I = v.findViewById(R.id.addImage);
            Delete_Button(deleteItem);
            Image_Button(I);
            bitmaps.add(null);
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
                                EditSetOperations.DeleteSet(ISEDIT, cnt, ReadImages);

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

                    EditSetOperations.SaveSetThread(cnt, TableName, repeatsAddEditActivity, bitmaps, ReadImages, IgnoreChars, DB, false);
                    EditSetOperations.DeleteOldSet(ISEDIT, cnt, ImgToDelete);
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
                    final String TableName = name.getText().toString();
                    EditSetOperations.SaveSetThread(cnt, TableName, repeatsAddEditActivity, bitmaps, ReadImages, IgnoreChars, DB, true);
                    EditSetOperations.DeleteOldSet(ISEDIT, cnt, ImgToDelete);
                    ShareButton.ShareClick(cnt, TableName, NewName, activity);
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
        int elementIndex = Integer.parseInt(pView.getTag().toString());
        ImgToDelete.add(tag);
        ReadImages.remove(tag);
        bitmaps.set(elementIndex, null);

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
                    int e = Integer.parseInt(view.getTag().toString());

                    if(imgView.getVisibility() == View.VISIBLE)
                    {
                        String TAG = imgView.getTag().toString();
                        ImgToDelete.add(TAG);
                        ReadImages.remove(TAG);
                        bitmaps.set(e, null);
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

                    int elementIndex = Integer.parseInt(rel.getTag().toString());

                    bitmaps.set(elementIndex, selected);
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
                            View pView = (View)v.getParent();

                            int e = Integer.parseInt(pView.getTag().toString());
                            bitmaps.set(e, null);

                            ImageView img = pView.findViewById(R.id.imageView);
                            ImageButton imgBut = pView.findViewById(R.id.deleteImage);
                            ImageButton imgAdd = pView.findViewById(R.id.addImage);
                            img.setVisibility(View.GONE);
                            img.setImageBitmap(null);
                            img.setTag(null);
                            imgBut.setVisibility(View.GONE);
                            imgAdd.setEnabled(true);
                        }
                    });

                }
                catch (FileNotFoundException e)
                {
                    e.printStackTrace();
                }
                catch (SecurityException e)
                {
                    e.printStackTrace();

                    Toast.makeText(this, R.string.imageError, Toast.LENGTH_LONG).show();
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    photoPickerIntent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(photoPickerIntent, 1);
                }
            }
        }
        else if(requestCode == 111)
        {
            RepeatsAddEditActivity.super.onBackPressed();
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
