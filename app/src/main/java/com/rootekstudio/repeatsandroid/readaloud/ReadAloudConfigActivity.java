package com.rootekstudio.repeatsandroid.readaloud;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.RepeatsSetInfo;
import com.rootekstudio.repeatsandroid.database.DatabaseHelper;

import java.util.List;

public class ReadAloudConfigActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_aloud_config);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DatabaseHelper DB = new DatabaseHelper(this);
        List<RepeatsSetInfo> setsInfo = DB.AllItemsLIST(-1);

        LinearLayout linearLayout = findViewById(R.id.linearSetsListReadAloud);

        for (int i = 0; i < setsInfo.size(); i++) {
            RepeatsSetInfo setInfo = setsInfo.get(i);
            View view = LayoutInflater.from(this).inflate(R.layout.single_textview, null);
            view.setTag(setInfo.getTableName());

            TextView textView = view.findViewById(R.id.singleTextView);
            textView.setText(setInfo.getitle());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String setID = view.getTag().toString();
                    Intent intent = new Intent(ReadAloudConfigActivity.this, ReadAloudActivity.class);
                    intent.putExtra("setID", setID);
                    intent.putExtra("newReadAloud", true);
                    startActivity(intent);
                }
            });
            linearLayout.addView(view);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }
}
