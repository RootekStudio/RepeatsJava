package com.rootekstudio.repeatsandroid.search;

import android.animation.TimeInterpolator;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.transition.platform.MaterialContainerTransform;
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback;
import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.RepeatsHelper;
import com.rootekstudio.repeatsandroid.UIHelper;
import com.rootekstudio.repeatsandroid.database.MigrateDatabase;
import com.rootekstudio.repeatsandroid.database.RepeatsDatabase;
import com.rootekstudio.repeatsandroid.database.SetSingleItem;
import com.rootekstudio.repeatsandroid.database.SingleSetInfo;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    SearchAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(MigrateDatabase.oldDBExists()) {
            AlertDialog dialog = UIHelper.loadingDialog(getString(R.string.dataMigrate), this);
            dialog.show();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    new MigrateDatabase(SearchActivity.this).migrateToNewDatabase();
                    dialog.cancel();
                    startActivity(new Intent(SearchActivity.this, SearchActivity.class));
                    finish();
                }
            }).start();

            return;
        }

        UIHelper.DarkTheme(this, false);

        setContentView(R.layout.activity_search);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RepeatsDatabase DB = RepeatsDatabase.getInstance(this);
        List<SearchItem> sItem = new ArrayList<>();

        List<SingleSetInfo> list = DB.allSetsInfo(-1);
        int count = list.size();

        for (int i = 0; i < count; i++) {
            SingleSetInfo singleitem = list.get(i);
            String TableName = singleitem.getSetID();
            String title = singleitem.getSetName();

            List<SetSingleItem> single = DB.allItemsInSet(TableName, -1);

            int singlecount = single.size();
            for (int j = 0; j < singlecount; j++) {
                SetSingleItem singleSetDB = single.get(j);
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
