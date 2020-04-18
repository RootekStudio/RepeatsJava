package com.rootekstudio.repeatsandroid.search;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;

import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.RepeatsHelper;
import com.rootekstudio.repeatsandroid.RepeatsSetInfo;
import com.rootekstudio.repeatsandroid.RepeatsSingleItem;
import com.rootekstudio.repeatsandroid.database.DatabaseHelper;
import com.rootekstudio.repeatsandroid.search.SearchAdapter;
import com.rootekstudio.repeatsandroid.search.SearchItem;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    SearchAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RepeatsHelper.DarkTheme(this, false);

        setContentView(R.layout.activity_search);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DatabaseHelper DB = new DatabaseHelper(this);
        List<SearchItem> sItem = new ArrayList<>();

        List<RepeatsSetInfo> list = DB.AllItemsLIST(-1);
        int count = list.size();

        for (int i = 0; i < count; i++) {
            RepeatsSetInfo singleitem = list.get(i);
            String TableName = singleitem.getTableName();
            String title = singleitem.getitle();

            List<RepeatsSingleItem> single = DB.AllItemsSET(TableName, -1);

            int singlecount = single.size();
            for (int j = 0; j < singlecount; j++) {
                RepeatsSingleItem singleSetDB = single.get(j);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        adapter.search(newText);
        return false;
    }
}
