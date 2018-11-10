package com.rootekstudio.repeatsandroid;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

public class RepeatsAddEditActivity extends AppCompatActivity
{
    public static String TITLE;
    private DatabaseHelper DB;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repeats_add_edit);

        final Button add = findViewById(R.id.addLayout);
        final Button save = findViewById(R.id.saveButton);

        DB = new DatabaseHelper(this);

        final Intent intent = new Intent(this, MainActivity.class);

        final LayoutInflater inflater = LayoutInflater.from(this);
        add.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                ViewGroup parent = findViewById(R.id.AddRepeatsLinear);
                inflater.inflate(R.layout.addrepeatslistitem, parent);
            }
        });

        save.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                final ViewGroup par = findViewById(R.id.AddRepeatsLinear);
                int itemscount = par.getChildCount();
                itemscount--;

                SimpleDateFormat s = new SimpleDateFormat("yyyyMMddHHmmss");
                String SetName = "R" + s.format(new Date());
                TITLE = SetName;

                DB.CreateSet(SetName);

                for (int i = 0; i <= itemscount; i++)
                {
                    View v = par.getChildAt(i);

                    EditText q = v.findViewById(R.id.questionBox);
                    EditText a = v.findViewById(R.id.answerBox);
                    String question = q.getText().toString();
                    String answer = a.getText().toString();
                    String image = "";

                    RepeatsSingleSetDB set = new RepeatsSingleSetDB(question, answer, image);
                    DB.AddSet(set);
                }

                EditText name = findViewById(R.id.projectname);
                String TableName = name.getText().toString();

                SimpleDateFormat s1 = new SimpleDateFormat("dd.MM.yyyy");
                String CreateDate = s1.format(new Date());

                RepeatsListDB ListDB = new RepeatsListDB(SetName, TableName, CreateDate, "true", "test");
                DB.AddName(ListDB);

                startActivity(intent);
            }
        });


    }
}
