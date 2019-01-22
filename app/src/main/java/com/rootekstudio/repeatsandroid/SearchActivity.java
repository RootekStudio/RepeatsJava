package com.rootekstudio.repeatsandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

class SearchItem
{
    String Question;
    String Answer;
    String Title;
    String TableName;

    public SearchItem(String Question, String Answer, String Title, String TableName)
    {
        this.Question = Question;
        this.Answer = Answer;
        this.Title = Title;
        this.TableName = TableName;
    }

    String getQuestion() { return Title; }
    String getAnswer() { return Answer; }

}

public class SearchActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        List<SearchItem>sItem = new ArrayList<>();


        SearchItem item = new SearchItem("Q", "A", "T", "A");
        sItem.add(item);

        Boolean ABC = sItem.contains(item);
        int a = 0;

//        HashMap QandA = new HashMap<String, String>();
//        QandA.put("A", "A");
//        QandA.


    }
}
