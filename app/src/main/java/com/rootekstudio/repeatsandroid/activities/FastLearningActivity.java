package com.rootekstudio.repeatsandroid.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.rootekstudio.repeatsandroid.FLCore;
import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.RepeatsHelper;
import com.rootekstudio.repeatsandroid.RepeatsSingleSetDB;
import com.rootekstudio.repeatsandroid.database.DatabaseHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

public class FastLearningActivity extends AppCompatActivity {
    TextView question;
    TextView correctA;
    EditText editText;
    static Button button;
    ImageView badge;
    ImageView image;
    LinearLayout linearQbox;

    FLCore FL;

    Context context;

    String setID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean isDark = RepeatsHelper.DarkTheme(this, false);

        setContentView(R.layout.fast_learning);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        context = this;

        FL = new FLCore();

        Intent intent = getIntent();

        setID = intent.getStringExtra("TableName");

        DatabaseHelper DB = new DatabaseHelper(this);
        List<RepeatsSingleSetDB> itemsSetList = DB.AllItemsSET(setID);

        FL.start(itemsSetList);

        question = findViewById(R.id.questionFL);
        correctA = findViewById(R.id.correctAnswerFL);
        editText = findViewById(R.id.editTextFL);
        button = findViewById(R.id.buttonFL);
        badge = findViewById(R.id.badgeFL);
        image = findViewById(R.id.imageViewFL);
        linearQbox = findViewById(R.id.linearQuestionBox);

        if (!isDark) {
            linearQbox.setBackgroundResource(R.drawable.layout_mainshape);
            editText.setBackgroundResource(R.drawable.edittext_shape);
        }

        loadQuestion();
    }

    void loadQuestion() {
        if (!FL.done) {
            //reset
            editText.setText("");
            correctA.setText("");
            correctA.setVisibility(View.GONE);
            badge.setVisibility(View.GONE);
            editText.setEnabled(true);
            image.setImageBitmap(null);
            image.setVisibility(View.GONE);

            final String[] qanda = FLCore.gQuestionAndAnswer();
            question.setText(qanda[0]);

            String img = qanda[2];
            if (!img.equals("")) {
                image.setVisibility(View.VISIBLE);

                File file = new File(getFilesDir(), img);
                FileInputStream inputStream = null;
                try {
                    inputStream = new FileInputStream(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);


                image.setImageBitmap(bitmap);
            }

            button.setText(R.string.Check);
            button.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_check_box, 0, 0, 0);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String answer = editText.getText().toString();
                    editText.setEnabled(false);

                    Button button = (Button) view;
                    button.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_navigate_next, 0, 0, 0);
                    button.setText(R.string.Next);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            loadQuestion();
                        }
                    });

                    badge.setVisibility(View.VISIBLE);
                    correctA.setVisibility(View.VISIBLE);
                    if (!FL.checkAnswered(answer)) {
                        String notCorrect = getString(R.string.IncorrectAnswer2) + ": " + qanda[1];
                        badge.setImageResource(R.drawable.ic_clear);
                        badge.setColorFilter(ContextCompat.getColor(context, android.R.color.holo_red_dark));
                        correctA.setText(notCorrect);
                    } else {
                        correctA.setText(R.string.CorrectAnswer2);
                        badge.setImageResource(R.drawable.ic_check);
                        badge.setColorFilter(ContextCompat.getColor(context, android.R.color.holo_green_light));
                    }
                }
            });
        } else {
            end();
        }
    }

    void end() {
        linearQbox.setVisibility(View.INVISIBLE);
        correctA.setText(R.string.FLendText);
        button.setText(R.string.OnceAgain);
        badge.setVisibility(View.INVISIBLE);
        button.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_refresh, 0, 0, 0);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent intent = new Intent(context, FastLearningActivity.class);
                intent.putExtra("TableName", setID);
                startActivity(intent);
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
