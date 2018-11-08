package com.rootekstudio.repeatsandroid;

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

public class RepeatsAddEditActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repeats_add_edit);

        final Button add = findViewById(R.id.addLayout);
        final Button save = findViewById(R.id.saveButton);

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
                EditText name = view.findViewById(R.id.projectname);
                final ViewGroup par = view.findViewById(R.id.AddRepeatsLinear);
                int itemscount = par.getChildCount();
                itemscount--;

                for (int i = 0; i <= itemscount; i++)
                {
                    View v = par.getChildAt(i);

                    EditText q = v.findViewById(R.id.questionBox);
                    EditText a = v.findViewById(R.id.answerBox);
                    String question = q.getText().toString();
                    String answer = a.getText().toString();
                }
            }
        });
    }
}
