package com.rootekstudio.repeatsandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TestResults extends AppCompatActivity
{
    List<String> Headers;
    HashMap<String, List<String>> Children;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_results);

        Intent intent = getIntent();
        int all = intent.getIntExtra("All", 0);
        int CountCorrect = intent.getIntExtra("Correct", 0);
        int CountIncorrect = intent.getIntExtra("Incorrect", 0);

        TextView txtPercent = findViewById(R.id.percentScore);
        TextView txtSummary = findViewById(R.id.summary);

        int percent = (CountCorrect * 100) / all;

        String txt1 = percent + "%";

        txtPercent.setText(txt1);

        String txt2 =
                getString(R.string.CorrectAnswers) + " " + Integer.toString(CountCorrect)
                + System.getProperty ("line.separator")
                + getString(R.string.IncorrectAnswers) + " " + Integer.toString(CountIncorrect)
                + System.getProperty ("line.separator")
                + getString(R.string.AllAnswers) + " " + Integer.toString(all);

        txtSummary.setText(txt2);

        LoadList();

        ExpandableListView expandableListView = findViewById(R.id.Expandable);
        ExpandableListAdapter adapter = new ExpandableListAdapter(this, Headers, Children);
        expandableListView.setAdapter(adapter);
    }

    void LoadList()
    {
        List<String> AllQuestions = TestActivity.AllQuestions;
        List<String> UserAnswers = TestActivity.UserAnswers;
        List<String> CorrectAnswers = TestActivity.CorrectAnswers;

        Children = new HashMap<>();
        Headers = AllQuestions;

        int count = AllQuestions.size();
        for(int i = 0; i < count; i++)
        {
            List<String> UserAndCorrect = new ArrayList<>();
            UserAndCorrect.add(getString(R.string.CorrectAnswer) + " " + CorrectAnswers.get(i));
            UserAndCorrect.add(getString(R.string.YourAnswer) + " " + UserAnswers.get(i));

            Children.put(AllQuestions.get(i), UserAndCorrect);
        }
    }
}
