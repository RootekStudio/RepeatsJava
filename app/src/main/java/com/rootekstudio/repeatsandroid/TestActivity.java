package com.rootekstudio.repeatsandroid;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class TestActivity extends AppCompatActivity
{
    LinearLayout linearLayout;
    Boolean IsDark;
    String ignore = "false";
    static List<String> AllQuestions;
    static List<String> UserAnswers;
    static List<String> CorrectAnswers;
    static List<Boolean> IsCorrect;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        IsDark = RepeatsHelper.DarkTheme(this);
        setContentView(R.layout.activity_test);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        linearLayout = findViewById(R.id.LinearTest);
        Intent thisintent = getIntent();
        String TableName = thisintent.getStringExtra("TableName");
        String title = thisintent.getStringExtra("title");
        ignore = thisintent.getStringExtra("IgnoreChars");
        DatabaseHelper DB = new DatabaseHelper(this);
        List<RepeatsSingleSetDB> Single = DB.AllItemsSET(TableName);
        int count = Single.size();

        if(!IsDark)
        {
            BottomAppBar appBar = findViewById(R.id.barTest);
            appBar.setBackgroundTint(ContextCompat.getColorStateList(this, R.color.DayColorPrimaryDark));
        }

        for(int i = 0; i < count; i++)
        {
            getLayoutInflater().inflate(R.layout.test_item, linearLayout);
            View view = linearLayout.getChildAt(i);

            RepeatsSingleSetDB set = Single.get(i);
            String Question = set.getQuestion();
            String Answer = set.getAnswer();
            String Image = set.getImag();

            RelativeLayout relativeLayout = view.findViewById(R.id.RelativeTitem);

            if(IsDark)
            {
                relativeLayout.setBackgroundResource(R.drawable.layout_mainshape_dark);
            }
            else
            {
                relativeLayout.setBackgroundResource(R.drawable.layout_mainshape);
            }

            if(!Image.equals(""))
            {
                File file = new File(getFilesDir(), Image);
                FileInputStream inputStream = null;
                try
                {
                    inputStream = new FileInputStream(file);
                }
                catch (FileNotFoundException e)
                {
                    e.printStackTrace();
                }

                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                ImageView imgView = view.findViewById(R.id.imageViewTest);
                imgView.setImageBitmap(bitmap);
                imgView.setVisibility(View.VISIBLE);
            }

            TextView TextQuestion = view.findViewById(R.id.TestQuestion);
            EditText EditAnswer = view.findViewById(R.id.TestAnswer);
            TextQuestion.setText(Question);
            EditAnswer.setTag(Answer);
        }

        FloatingActionButton fab = findViewById(R.id.fabTest);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                CheckTest();
            }
        });

    }

    void CheckTest()
    {
        AllQuestions = new ArrayList<>();
        UserAnswers = new ArrayList<>();
        CorrectAnswers = new ArrayList<>();
        IsCorrect = new ArrayList<>();

        int CountCorrect = 0;
        int CountIncorrect = 0;

        int count = linearLayout.getChildCount();

        for(int i = 0; i < count; i++)
        {
            View child = linearLayout.getChildAt(i);
            TextView q = child.findViewById(R.id.TestQuestion);
            EditText e = child.findViewById(R.id.TestAnswer);


            String question = q.getText().toString();
            String user = e.getText().toString();
            String correct = e.getTag().toString();
            String reallyCorrect = correct;
            String reallyUser = user;

            if(ignore.equals("true"))
            {
                user = Normalizer.normalize(user, Normalizer.Form.NFD)
                        .replaceAll(" ", "")
                        .replaceAll("Ł","l")
                        .replaceAll("ł", "l")
                        .replaceAll("[^\\p{ASCII}]", "")
                        .toLowerCase(Locale.getDefault());

                correct = Normalizer.normalize(correct, Normalizer.Form.NFD)
                        .replaceAll(" ", "")
                        .replaceAll("Ł","l")
                        .replaceAll("ł", "l")
                        .replaceAll("[^\\p{ASCII}]", "")
                        .toLowerCase(Locale.getDefault());
            }

            if(user.equals(correct))
            {
                CountCorrect++;
                IsCorrect.add(true);
            }
            else
            {
                CountIncorrect++;
                IsCorrect.add(false);
            }

            AllQuestions.add(question);
            UserAnswers.add(reallyUser);
            CorrectAnswers.add(reallyCorrect);
        }

        Intent intent = new Intent(this, TestResults.class);
        intent.putExtra("Correct", CountCorrect);
        intent.putExtra("Incorrect", CountIncorrect);
        intent.putExtra("All", count);

        finish();
        startActivity(intent);
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
            new AlertDialog.Builder(this)
                    .setMessage(R.string.WantLeaveTest)
                    .setNegativeButton(R.string.Cancel, null)
                    .setPositiveButton(R.string.Leave, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            TestActivity.super.onBackPressed();
                        }
                    }).create().show();

    }
}
