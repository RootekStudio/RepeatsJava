package com.rootekstudio.repeatsandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TestActivity extends AppCompatActivity
{
    LinearLayout linearLayout;
    static List<String> AllQuestions;
    static List<String> UserAnswers;
    static List<String> CorrectAnswers;
    static List<Boolean> IsCorrect;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        Timer timer = new Timer();

        linearLayout = findViewById(R.id.LinearTest);
        Intent thisintent = getIntent();
        String TableName = thisintent.getStringExtra("TableName");
        String title = thisintent.getStringExtra("title");
        DatabaseHelper DB = new DatabaseHelper(this);
        List<RepeatsSingleSetDB> Single = DB.AllItemsSET(TableName);
        int count = Single.size();

        for(int i = 0; i < count; i++)
        {
            View view = getLayoutInflater().inflate(R.layout.testitem, null);

            RepeatsSingleSetDB set = Single.get(i);
            String Question = set.getQuestion();
            String Answer = set.getAnswer();

            TextView TextQuestion = view.findViewById(R.id.TestQuestion);
            EditText EditAnswer = view.findViewById(R.id.TestAnswer);
            TextQuestion.setText(Question);
            EditAnswer.setTag(Answer);

            linearLayout.addView(view);
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
            UserAnswers.add(user);
            CorrectAnswers.add(correct);
        }

        Intent intent = new Intent(this, TestResults.class);
        intent.putExtra("Correct", CountCorrect);
        intent.putExtra("Incorrect", CountIncorrect);
        intent.putExtra("All", count);

        finish();
        startActivity(intent);
    }
}
