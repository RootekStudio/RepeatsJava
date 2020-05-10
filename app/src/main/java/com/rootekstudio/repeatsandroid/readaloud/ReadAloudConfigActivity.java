package com.rootekstudio.repeatsandroid.readaloud;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.UIHelper;
import com.rootekstudio.repeatsandroid.database.MigrateDatabase;
import com.rootekstudio.repeatsandroid.database.RepeatsDatabase;
import com.rootekstudio.repeatsandroid.database.SingleSetInfo;

import java.util.List;

public class ReadAloudConfigActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (MigrateDatabase.oldDBExists()) {
            AlertDialog dialog = UIHelper.loadingDialog(getString(R.string.dataMigrate), this);
            dialog.show();

            new Thread(() -> {
                new MigrateDatabase(ReadAloudConfigActivity.this).migrateToNewDatabase();
                dialog.cancel();
                startActivity(new Intent(ReadAloudConfigActivity.this, ReadAloudConfigActivity.class));
                finish();
            }).start();

            return;
        }

        setContentView(R.layout.activity_read_aloud_config);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RepeatsDatabase DB = RepeatsDatabase.getInstance(this);
        List<SingleSetInfo> setsInfo = DB.allSetsInfo(-1);

        LinearLayout linearLayout = findViewById(R.id.linearSetsListReadAloud);

        for (int i = 0; i < setsInfo.size(); i++) {
            SingleSetInfo setInfo = setsInfo.get(i);
            View view = LayoutInflater.from(this).inflate(R.layout.single_textview, null);
            view.setTag(setInfo.getSetID());

            TextView textView = view.findViewById(R.id.singleTextView);
            textView.setText(setInfo.getSetName());

            view.setOnClickListener(view1 -> {
                String setID = view1.getTag().toString();
                Intent intent = new Intent(ReadAloudConfigActivity.this, ReadAloudActivity.class);
                intent.putExtra("setID", setID);
                intent.putExtra("newReadAloud", true);
                startActivity(intent);
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
