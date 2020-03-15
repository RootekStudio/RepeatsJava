package com.rootekstudio.repeatsandroid.statistics;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.statistics.SetStats;
import com.rootekstudio.repeatsandroid.statistics.StatsActivityAdapter;
import com.rootekstudio.repeatsandroid.database.DatabaseHelper;

import java.util.List;

public class StatsActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    DatabaseHelper DB;
    int usableWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DB = new DatabaseHelper(this);
        usableWidth = getUsableWidth();
        recyclerView = findViewById(R.id.recyclerViewStats);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        final TextView goodAnswers = findViewById(R.id.goodAnswersCountStats);
        final TextView wrongAnswers = findViewById(R.id.wrongAnswersCountStats);
        final TextView allAnswers = findViewById(R.id.allAnswersCountStats);
        final ProgressBar progressBar = findViewById(R.id.progressBarLoadingStats);

        new Thread(new Runnable() {
            @Override
            public void run() {
                final String goodAnswersString = String.valueOf(DB.columnSum("TitleTable", "goodAnswers"));
                final String wrongAnswersString = String.valueOf(DB.columnSum("TitleTable", "wrongAnswers"));
                final String allAnswersString = String.valueOf(DB.columnSum("TitleTable", "allAnswers"));
                List<SetStats> setsStats = DB.selectSetsStatsInfo(0);
                adapter = new StatsActivityAdapter(setsStats, usableWidth);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        goodAnswers.setText(goodAnswersString);
                        wrongAnswers.setText(wrongAnswersString);
                        allAnswers.setText(allAnswersString);
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
                    List<SetStats> setsStats = DB.selectSetsStatsInfo(0);
                    adapter = new StatsActivityAdapter(setsStats, usableWidth);
                    recyclerView.setAdapter(adapter);
                } else if (item.getItemId() == R.id.sortWrongAnswers) {
                    List<SetStats> setsStats = DB.selectSetsStatsInfo(1);
                    adapter = new StatsActivityAdapter(setsStats, usableWidth);
                    recyclerView.setAdapter(adapter);
                } else if (item.getItemId() == R.id.sortCreationDateAscending) {
                    List<SetStats> setsStats = DB.selectSetsStatsInfo(2);
                    adapter = new StatsActivityAdapter(setsStats, usableWidth);
                    recyclerView.setAdapter(adapter);

                } else if (item.getItemId() == R.id.sortCreationDateDescending) {
                    List<SetStats> setsStats = DB.selectSetsStatsInfo(3);
                    adapter = new StatsActivityAdapter(setsStats, usableWidth);
                    recyclerView.setAdapter(adapter);
                }
                return true;
            }
        });
    }

    private int getUsableWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowmanager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        assert windowmanager != null;
        windowmanager.getDefaultDisplay().getMetrics(displayMetrics);


        float density = displayMetrics.density;
        float widthDp = displayMetrics.widthPixels / density;
        float relativeWidthDp = widthDp - 50;
        return Math.round(relativeWidthDp * density);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }
}