package com.rootekstudio.repeatsandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DatabaseHelper DB = new DatabaseHelper(this);

        final Intent intent = new Intent(this, RepeatsAddEditActivity.class);

        ListView listView = findViewById(R.id.AllSetsList);


        List<RepeatsListDB> ALL  = DB.AllItemsLIST();
        int ItemsCounts = ALL.size();

        String[] Names = new String[]{};

        ArrayList<String> List = new ArrayList<String>();

        for(int i = 0; i < ItemsCounts; i++)
        {
            String Name = ALL.get(i).getTableName();
            List.add(Name);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, List);

        listView.setAdapter(adapter);

        final Button btn = findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                startActivity(intent);
            }
        });
    }
}
