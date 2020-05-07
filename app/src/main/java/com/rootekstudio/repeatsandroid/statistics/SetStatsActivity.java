package com.rootekstudio.repeatsandroid.statistics;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.database.RepeatsDatabase;
import com.rootekstudio.repeatsandroid.database.SetSingleItem;
import com.rootekstudio.repeatsandroid.database.Values;

import java.util.List;

public class SetStatsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    String setID;
    String setName;
    RepeatsDatabase DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_stats);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DB = new RepeatsDatabase(this);
        Intent intent = getIntent();
        setID = intent.getStringExtra("setID");
        setName = intent.getStringExtra("setName");

        TextView setNameTextView = findViewById(R.id.setNameSetStats);
        final TextView goodAnswersTextView = findViewById(R.id.goodAnswersCountSetStats);
        final TextView wrongAnswersTextView = findViewById(R.id.wrongAnswersCountSetStats);
        final TextView allAnswersTextView = findViewById(R.id.allAnswersCountSetStats);
        final ProgressBar progressBar = findViewById(R.id.progressBarLoadingSetStats);

        setNameTextView.setText(setName);

        recyclerView = findViewById(R.id.recyclerViewSetStats);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));


        new Thread(new Runnable() {
            @Override
            public void run() {
                final int goodAnswers = DB.columnSum(setID, Values.good_answers);
                final int wrongAnswers = DB.columnSum(setID, Values.wrong_answers);
                final int allAnswers = goodAnswers + wrongAnswers;
                List<SetSingleItem> setsStats = DB.allItemsInSet(setID, Values.ORDER_BY_GOOD_ANSWERS_DESC);
                setsStats.add(0, new SetSingleItem());
                adapter = new SetStatsActivityAdapter(setsStats);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        goodAnswersTextView.setText(String.valueOf(goodAnswers));
                        wrongAnswersTextView.setText(String.valueOf(wrongAnswers));
                        allAnswersTextView.setText(String.valueOf(allAnswers));
                        recyclerView.setAdapter(adapter);
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        }).start();


    }

    public void sortStatsClick(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.sort_options, popupMenu.getMenu());
        MenuPopupHelper menuPopupHelper = new MenuPopupHelper(this, (MenuBuilder) popupMenu.getMenu(), view);
        menuPopupHelper.setForceShowIcon(true);
        menuPopupHelper.show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.sortGoodAnswers) {
                    List<SetSingleItem> setsStats = DB.allItemsInSet(setID, Values.ORDER_BY_GOOD_ANSWERS_DESC);
                    setsStats.add(0, new SetSingleItem());
                    adapter = new SetStatsActivityAdapter(setsStats);
                    recyclerView.setAdapter(adapter);
                } else if (item.getItemId() == R.id.sortWrongAnswers) {
                    List<SetSingleItem> setsStats = DB.allItemsInSet(setID, Values.ORDER_BY_WRONG_ANSWERS_DESC);
                    setsStats.add(0, new SetSingleItem());
                    adapter = new SetStatsActivityAdapter(setsStats);
                    recyclerView.setAdapter(adapter);
                } else if (item.getItemId() == R.id.sortCreationDateAscending) {
                    List<SetSingleItem> setsStats = DB.allItemsInSet(setID, Values.ORDER_BY_ID_ASC);
                    setsStats.add(0, new SetSingleItem());
                    adapter = new SetStatsActivityAdapter(setsStats);
                    recyclerView.setAdapter(adapter);
                } else if (item.getItemId() == R.id.sortCreationDateDescending) {
                    List<SetSingleItem> setsStats = DB.allItemsInSet(setID, Values.ORDER_BY_ID_DESC);
                    setsStats.add(0, new SetSingleItem());
                    adapter = new SetStatsActivityAdapter(setsStats);
                    recyclerView.setAdapter(adapter);
                }
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }
}
