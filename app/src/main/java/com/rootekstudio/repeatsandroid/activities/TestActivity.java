package com.rootekstudio.repeatsandroid.activities;

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

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rootekstudio.repeatsandroid.CheckAnswer;
import com.rootekstudio.repeatsandroid.database.DatabaseHelper;
import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.RepeatsHelper;
import com.rootekstudio.repeatsandroid.RepeatsSingleSetDB;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class TestActivity extends AppCompatActivity {
    LinearLayout linearLayout;
    Boolean IsDark;
    String ignore = "false";
    static List<String> AllQuestions;
    static List<String> UserAnswers;
    static List<String> CorrectAnswers;
    static List<Boolean> IsCorrect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IsDark = RepeatsHelper.DarkTheme(this);
        setContentView(R.layout.activity_test);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        linearLayout = findViewById(R.id.LinearTest);
        Intent thisintent = getIntent();
        String TableName = thisintent.getStringExtra("TableName");
        ignore = thisintent.getStringExtra("IgnoreChars");
        DatabaseHelper DB = new DatabaseHelper(this);
        final List<RepeatsSingleSetDB> Single = DB.AllItemsSET(TableName);
        final int count = Single.size();

        if (!IsDark) {
            BottomAppBar appBar = findViewById(R.id.barTest);
            appBar.setBackgroundTint(ContextCompat.getColorStateList(this, R.color.DayColorPrimaryDark));
        }


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < count; i++) {
                    final RelativeLayout view = (RelativeLayout) getLayoutInflater().inflate(R.layout.test_item, linearLayout, false);

                    RepeatsSingleSetDB set = Single.get(i);
                    String Question = set.getQuestion();
                    String Answer = set.getAnswer();
                    String Image = set.getImag();

                    if (IsDark) {
                        view.setBackgroundResource(R.drawable.layout_mainshape_dark);
                    } else {
                        view.setBackgroundResource(R.drawable.layout_mainshape);
                    }

                    if (!Image.equals("")) {
                        File file = new File(getFilesDir(), Image);
                        FileInputStream inputStream = null;
                        try {
                            inputStream = new FileInputStream(file);
                        } catch (FileNotFoundException e) {
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

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            linearLayout.addView(view);
                        }
                    });
                }
            }
        });
        thread.start();

        FloatingActionButton fab = findViewById(R.id.fabTest);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckTest();
            }
        });

    }

    void CheckTest() {
        AllQuestions = new ArrayList<>();
        UserAnswers = new ArrayList<>();
        CorrectAnswers = new ArrayList<>();
        IsCorrect = new ArrayList<>();

        int CountCorrect = 0;
        int CountIncorrect = 0;

        int count = linearLayout.getChildCount();

        for (int i = 0; i < count; i++) {
            View child = linearLayout.getChildAt(i);
            TextView q = child.findViewById(R.id.TestQuestion);
            EditText e = child.findViewById(R.id.TestAnswer);

            String question = q.getText().toString();
            String user = e.getText().toString();
            String correct = e.getTag().toString();

            boolean check = CheckAnswer.isAnswerCorrect(user, correct, ignore);

            if(check) {
                CountCorrect++;
                IsCorrect.add(true);
            }
            else {
                CountIncorrect++;
                IsCorrect.add(false);
            }

            AllQuestions.add(question);
            UserAnswers.add(user);
            CorrectAnswers.add(correct);
        }

        Intent intent = new Intent(this, TestResultsActivity.class);
        intent.putExtra("Correct", CountCorrect);
        intent.putExtra("Incorrect", CountIncorrect);
        intent.putExtra("All", count);

        finish();
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.WantLeaveTest)
                .setNegativeButton(R.string.Cancel, null)
                .setPositiveButton(R.string.Leave, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TestActivity.super.onBackPressed();
                    }
                }).create().show();

    }
}
