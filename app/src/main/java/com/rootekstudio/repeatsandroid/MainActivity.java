package com.rootekstudio.repeatsandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

        final ListView listView = findViewById(R.id.AllSetsList);

        final List<RepeatsListDB> ALL  = DB.AllItemsLIST();
        int ItemsCounts = ALL.size();

        ArrayList<String> List = new ArrayList<String>();

        for(int i = 0; i < ItemsCounts; i++)
        {
            String Name = ALL.get(i).getTableName();
            List.add(Name);
        }

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, List);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                String TITLE = ALL.get(position).getitle();
                String TABLE_NAME = ALL.get(position).getTableName();
                intent.putExtra("ISEDIT", TITLE);
                intent.putExtra("NAME", TABLE_NAME);
                startActivity(intent);
            }
        });

        final Button btn = findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                intent.putExtra("ISEDIT", "FALSE");
                startActivity(intent);
            }
        });
    }
}
