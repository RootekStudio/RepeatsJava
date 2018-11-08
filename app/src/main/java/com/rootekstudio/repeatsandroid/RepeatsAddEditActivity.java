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

public class RepeatsAddEditActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repeats_add_edit);

        final Button btn = findViewById(R.id.button2);

        final Button btn5 = findViewById(R.id.button5);
        final LayoutInflater inflater = LayoutInflater.from(this);
        btn.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                ViewGroup parent = findViewById(R.id.AddRepeatsLinear);
                inflater.inflate(R.layout.addrepeatslistitem, parent);
            }
        });
    }

}
