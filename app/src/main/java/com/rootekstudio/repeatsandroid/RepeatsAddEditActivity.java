package com.rootekstudio.repeatsandroid;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.renderscript.ScriptGroup;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

public class RepeatsAddEditActivity extends AppCompatActivity
{
    public static String TITLE;
    private ImageView imageView;
    private DatabaseHelper DB;
    private ViewGroup parent;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repeats_add_edit);

        final Context cnt = this;
        DB = new DatabaseHelper(cnt);

        BottomAppBar bottomAppBar = findViewById(R.id.AddQuestionBar);
        bottomAppBar.replaceMenu(R.menu.bottomappbar_addset);

        parent = findViewById(R.id.AddRepeatsLinear);

        final LayoutInflater inflater = LayoutInflater.from(cnt);
        final Intent intent = new Intent(cnt, MainActivity.class);
        final EditText name = findViewById(R.id.projectname);

        final AlertDialog.Builder ALERTbuilder = new AlertDialog.Builder(cnt);

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
                Button B = v2.findViewById(R.id.deleteItem);
                Button I = v2.findViewById(R.id.addImage);
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

                EditText Q = child.findViewById(R.id.questionBox);
                EditText A = child.findViewById(R.id.answerBox);
                Button B = child.findViewById(R.id.deleteItem);
                Button I = child.findViewById(R.id.addImage);
                ImageView img = child.findViewById(R.id.imageView);
                Delete_Button(B);
                Image_Button(I);

                if(!Image.equals(""))
                {
                    img.setVisibility(View.VISIBLE);
                    try
                    {
                        FileInputStream inputStream;
                        inputStream = openFileInput(Image);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        img.setImageBitmap(bitmap);
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
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
            final Button deleteItem = v.findViewById(R.id.deleteItem);
            Button I = v.findViewById(R.id.addImage);
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

                        startActivity(intent);
                    }
                }
                //endregion
                //region Save set
                else if(item.getItemId() == R.id.saveButton)
                {
                    final ViewGroup par = findViewById(R.id.AddRepeatsLinear);
                    int itemscount = par.getChildCount();
                    itemscount--;

                    SimpleDateFormat s = new SimpleDateFormat("yyyyMMddHHmmss");
                    String SetName = "R" + s.format(new Date());
                    String SetImage = SetName.replace("R", "I");
                    TITLE = SetName;

                    DB.CreateSet(SetName);

                    for (int i = 0; i <= itemscount; i++)
                    {
                        View v = par.getChildAt(i);

                        EditText q = v.findViewById(R.id.questionBox);
                        EditText a = v.findViewById(R.id.answerBox);
                        ImageView img = v.findViewById(R.id.imageView);
                        String question = q.getText().toString();
                        String answer = a.getText().toString();
//                    String image = img.getTag(R.string.Tag_id_0).toString();
//                    String imageName = img.getTag(R.string.Tag_id_1).toString();

//                    if(!image.equals(""))
//                    {
//                        Uri IMAGE = Uri.parse(image);
//
//                            Drawable drawable = img.getDrawable();
//                            BitmapDrawable bitdraw = (BitmapDrawable) drawable;
//                            Bitmap bitmap = bitdraw.getBitmap();
//
//                    }
                        RepeatsSingleSetDB set = new RepeatsSingleSetDB(question, answer, "");
                        DB.AddSet(set, TITLE);
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

                    RepeatsHelper.AskAboutTime(cnt, true);


                }
                //endregion

                return true;
            }
        });
    }

    //region Delete Single Question
    private void Delete_Button(Button button)
    {
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ViewParent view = v.getParent();
                int a = parent.indexOfChild((View) view);
                parent.removeViewAt(a);
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
//                String d = getPath(this, selectedImage);
                final InputStream imageStream;
                try
                {
//                    List<InputStream> inputStreams = new List<InputStream>();s
                    String PATH = getPath(getApplicationContext(), selectedImage);
                    imageView.setVisibility(View.VISIBLE);
                    String u = selectedImage.getPath();
                    imageStream = getContentResolver().openInputStream(selectedImage);
//                    inputStreams.add(imageStream);
                    final Bitmap selected = BitmapFactory.decodeStream(imageStream);

                    imageView.setImageBitmap(selected);

                    SimpleDateFormat s = new SimpleDateFormat("yyyyMMddHHmmss");
                    String SetName = "I" + s.format(new Date());

                    imageView.setTag(R.string.Tag_id_0, PATH);
                    imageView.setTag(R.string.Tag_id_1, SetName);
                }
                catch (FileNotFoundException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    private String getPath(Context context, Uri contentUri)
    {
        Cursor cursor = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(index);
    }

    private void Image_Button(Button button)
    {
//        button.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                ViewParent view = v.getParent();
//                RelativeLayout rel = (RelativeLayout) view;
//                imageView = rel.findViewById(R.id.imageView);
//
//                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(photoPickerIntent, 1);
//            }
//        });

    }
}
