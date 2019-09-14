package com.rootekstudio.repeatsandroid;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TestResults extends AppCompatActivity
{
    List<String> Headers;
    ExpandableListView expandableListView;
    HashMap<String, List<String>> Children;
    List<Integer> ToExpand = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        RepeatsHelper.DarkTheme(this);
        setContentView(R.layout.activity_test_results);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

        expandableListView = findViewById(R.id.Expandable);

        LoadList();

        ExpandableListAdapter adapter = new ExpandableListAdapter(this, Headers, Children);
        expandableListView.setAdapter(adapter);

        for(int i = 0; i < ToExpand.size(); i++)
        {
            int expand = ToExpand.get(i);
            expandableListView.expandGroup(expand);
        }
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
            String correct = CorrectAnswers.get(i);
            String userAnswer = UserAnswers.get(i);
            List<String> UserAndCorrect = new ArrayList<>();
            UserAndCorrect.add(getString(R.string.CorrectAnswer) + " " + correct);
            UserAndCorrect.add(getString(R.string.YourAnswer) + " " + userAnswer);

            if(!correct.equals(userAnswer))
            {
                ToExpand.add(i);
            }

            Children.put(AllQuestions.get(i), UserAndCorrect);
        }
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
}
