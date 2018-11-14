package com.rootekstudio.repeatsandroid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.strictmode.IntentReceiverLeakedViolation;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class RepeatsAddEditActivity extends AppCompatActivity
{
    public static String TITLE;
    private ImageView imageView;
    private DatabaseHelper DB;
    private ViewGroup parent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repeats_add_edit);

        final Button add = findViewById(R.id.addLayout);
        final Button save = findViewById(R.id.saveButton);
        final Button deleteALL = findViewById(R.id.deleteButton);
        parent = findViewById(R.id.AddRepeatsLinear);
        final LayoutInflater inflater = LayoutInflater.from(this);
        final Intent intent = new Intent(this, MainActivity.class);
        final EditText name = findViewById(R.id.projectname);

        Intent THISintent = getIntent();
        final String x = THISintent.getStringExtra("ISEDIT");
        final String n = THISintent.getStringExtra("NAME");

        DB = new DatabaseHelper(this);

        add.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ViewGroup parent = findViewById(R.id.AddRepeatsLinear);
                inflater.inflate(R.layout.addrepeatslistitem, parent);
                int items = parent.getChildCount();
                items--;
                View v = parent.getChildAt(items);
                Button B = v.findViewById(R.id.deleteItem);
                Button I = v.findViewById(R.id.addImage);
                Delete_Button(B);
                Image_Button(I);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                final ViewGroup par = findViewById(R.id.AddRepeatsLinear);
                int itemscount = par.getChildCount();
                itemscount--;

                SimpleDateFormat s = new SimpleDateFormat("yyyyMMddHHmmss");
                String SetName = "R" + s.format(new Date());
                TITLE = SetName;

                DB.CreateSet(SetName);

                for (int i = 0; i <= itemscount; i++) {
                    View v = par.getChildAt(i);

                    EditText q = v.findViewById(R.id.questionBox);
                    EditText a = v.findViewById(R.id.answerBox);
                    ImageView img = v.findViewById(R.id.imageView);
                    String question = q.getText().toString();
                    String answer = a.getText().toString();
                    String image = img.getTag().toString();

                    RepeatsSingleSetDB set = new RepeatsSingleSetDB(question, answer, image);
                    DB.AddSet(set);
                }


                String TableName = name.getText().toString();

                SimpleDateFormat s1 = new SimpleDateFormat("dd.MM.yyyy");
                String CreateDate = s1.format(new Date());

                RepeatsListDB ListDB = new RepeatsListDB(SetName, TableName, CreateDate, "true", "test");
                DB.AddName(ListDB);

                if (!x.equals("FALSE")) {
                    TITLE = x;
                    DB.deleteOneFromList(x);
                    DB.DeleteSet();
                }

                startActivity(intent);
            }
        });

        if (!x.equals("FALSE"))
        {
            name.setText(n);
            TITLE = x;
            List<RepeatsSingleSetDB> SET = DB.AllItemsSET();
            int ItemsCount = SET.size();

            for (int i = 0; i < ItemsCount; i++) {
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
                        Uri image = Uri.parse(Image);
                        final Bitmap selected = MediaStore.Images.Media.getBitmap(getContentResolver(), image);
                        img.setImageBitmap(selected);
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

                Q.setText(Question);
                A.setText(Answer);
            }
        }
        else
            {
            inflater.inflate(R.layout.addrepeatslistitem, parent);
            View v = parent.getChildAt(0);
            final Button deleteItem = v.findViewById(R.id.deleteItem);
            Button I = v.findViewById(R.id.addImage);
            Delete_Button(deleteItem);
            Image_Button(I);
            }

        deleteALL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DB.deleteOneFromList(x);
                DB.DeleteSet();
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == 1)
        {
            if (resultCode == RESULT_OK)
            {
                Uri selectedImage = data.getData();
                String d = getPath(this, selectedImage);
                Intent Data = data;
                Bundle ex = data.getExtras();
                File file = new File(String.valueOf(selectedImage));
                String ab = selectedImage.getPath();
//                File file = new File(String.valueOf(selectedImage));
                int i = 0;
                final InputStream imageStream;
                try
                {
                    imageView.setVisibility(View.VISIBLE);
                    String u = selectedImage.toString();
                    imageStream = getContentResolver().openInputStream(selectedImage);
                    final Bitmap selected = BitmapFactory.decodeStream(imageStream);

                    imageView.setImageBitmap(selected);
                    imageView.setTag(ab);
                }
                catch (FileNotFoundException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

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
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ViewParent view = v.getParent();
                RelativeLayout rel = (RelativeLayout) view;
                imageView = rel.findViewById(R.id.imageView);

                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(photoPickerIntent, 1);
            }
        });

    }
}
