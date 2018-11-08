package com.rootekstudio.repeatsandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Intent intent = new Intent(this, RepeatsAddEditActivity.class);

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
