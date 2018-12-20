package com.rootekstudio.repeatsandroid;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.media.ImageWriter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.SystemClock;
import android.os.strictmode.IntentReceiverLeakedViolation;
import android.provider.MediaStore;
import android.text.InputType;
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
        final AlertDialog.Builder ALERTbuilder = new AlertDialog.Builder(this);

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

        final Context cnt = this;

        save.setOnClickListener(new View.OnClickListener()
        {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            public void onClick(View view)
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
//                            File file = new File(image);
//
//                    }

                    RepeatsSingleSetDB set = new RepeatsSingleSetDB(question, answer, "");
                    DB.AddSet(set);
                }

                String TableName = name.getText().toString();
                SimpleDateFormat s1 = new SimpleDateFormat("dd.MM.yyyy");
                String CreateDate = s1.format(new Date());

                RepeatsListDB ListDB = new RepeatsListDB(SetName, TableName, CreateDate, "true", "test");
                DB.AddName(ListDB);

                if (!x.equals("FALSE"))
                {
                    TITLE = x;
                    DB.deleteOneFromList(x);
                    DB.DeleteSet();
                }

                createNotificationChannel();
                final View view1 = getLayoutInflater().inflate(R.layout.ask, null);
                final EditText editText = view1.findViewById(R.id.EditAsk);
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                ALERTbuilder.setView(view1);
                ALERTbuilder.setMessage(R.string.QuestionFreq);
                ALERTbuilder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {

                    }
                });

                ALERTbuilder.setPositiveButton(R.string.Save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Intent intent = new Intent(cnt, RepeatsQuestionSend.class);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(cnt, 0, intent, 0);
                        AlarmManager alarmManager = (AlarmManager)cnt.getSystemService(Context.ALARM_SERVICE);
                        String text = editText.getText().toString();
                        int time = Integer.parseInt(text);
                        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                                SystemClock.elapsedRealtime() + 1000 * 60 * time,
                                1000 * 60 * time,
                                pendingIntent);
                    }
                });

               ALERTbuilder.show();

            }
        });

        if (!x.equals("FALSE"))
        {
            name.setText(n);
            TITLE = x;
            List<RepeatsSingleSetDB> SET = DB.AllItemsSET(TITLE);
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

    private void createNotificationChannel()
    {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            CharSequence name =getString(R.string.ChannelTitle);
            String description = getString(R.string.ChannelDescription);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("CHANNEL_ID", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

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
                    String PATH = getPath(getApplicationContext(), selectedImage);
                    imageView.setVisibility(View.VISIBLE);
                    String u = selectedImage.getPath();
                    imageStream = getContentResolver().openInputStream(selectedImage);
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
