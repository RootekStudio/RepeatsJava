package com.rootekstudio.repeatsandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.widget.ListView;
import android.widget.SearchView;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

class SearchItem
{
    String Question;
    String Answer;
    String Title;
    String TableName;
    String AllItem;

    public SearchItem(String Question, String Answer, String Title, String TableName)
    {
        this.Question = Question;
        this.Answer = Answer;
        this.Title = Title;
        this.TableName = TableName;
        this.AllItem = Question + System.getProperty("line.separator") + Answer + System.getProperty("line.separator") + Title;
    }

    String gQuestion() { return Question; }
    String gAnswer() { return Answer; }
    String gTitle() { return Title; }
    String gTableName() {return TableName; }
    String gItem() {return AllItem; }

    public void sQuestion(String Question) { this.Question = Question; }
    public void sAnswer(String Answer) {this.Answer = Answer; }
    public void sTitle(String Title) {this.Title = Title; }
    public void sTableName(String TableName) {this.TableName = TableName;}
}

public class SearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener
{
    SearchAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        DatabaseHelper DB = new DatabaseHelper(this);
        List<SearchItem> sItem = new ArrayList<>();

        List<RepeatsListDB> list = DB.AllItemsLIST();
        int count = list.size();

        for (int i = 0; i < count; i++) {
            RepeatsListDB singleitem = list.get(i);
            String TableName = singleitem.TableName;
            String title = singleitem.Title;

            List<RepeatsSingleSetDB> single = DB.AllItemsSET(TableName);

            int singlecount = single.size();
            for (int j = 0; j < singlecount; j++) {
                RepeatsSingleSetDB singleSetDB = single.get(j);
                String Q = singleSetDB.getQuestion();
                String A = singleSetDB.getAnswer();

                sItem.add(new SearchItem(Q, A, title, TableName));
            }
        }

        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(this);
        ListView listView = findViewById(R.id.searchList);
        adapter = new SearchAdapter(this, sItem);
        listView.setAdapter(adapter);
    }


    @Override
    public boolean onQueryTextSubmit(String query)
    {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText)
    {
        adapter.search(newText);
        return false;
    }
}
