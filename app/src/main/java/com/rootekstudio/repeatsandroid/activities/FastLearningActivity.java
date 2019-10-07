package com.rootekstudio.repeatsandroid.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.rootekstudio.repeatsandroid.CheckAnswer;
import com.rootekstudio.repeatsandroid.FLCore;
import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.RepeatsSingleSetDB;
import com.rootekstudio.repeatsandroid.SingleFLitem;
import com.rootekstudio.repeatsandroid.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class FastLearningActivity extends AppCompatActivity {

    List<RepeatsSingleSetDB> itemsSetList;
    List<SingleFLitem> FLitems;

    TextView txt;
    EditText editText;
    Button button;

    int itemCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fast_learning);

        Intent intent = getIntent();

        String setID = intent.getStringExtra("TableName");

        DatabaseHelper DB = new DatabaseHelper(this);
        itemsSetList = DB.AllItemsSET(setID);
        FLitems = new ArrayList<>();

        txt = findViewById(R.id.textViewFL);
        editText = findViewById(R.id.editTextFL);
        button = findViewById(R.id.buttonFL);

        int setItemsCount = itemsSetList.size();
        setItemsCount-=10;

        for(int i = 0; i < 3; i++) {
            RepeatsSingleSetDB singleSet = itemsSetList.get(i);
            String question = singleSet.getQuestion();
            String answer = singleSet.getAnswer();

            FLitems.add(new SingleFLitem(question, answer, 0,0));
        }

        loadQuestion();
    }

    void loadQuestion() {
        final SingleFLitem FLitem = FLitems.get(itemCount);
        txt.setText(FLitem.FLquestion);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String answer = editText.getText().toString();

                if(CheckAnswer.isAnswerCorrect(answer, FLitem.getCorrectAnswer(), "false")) {
                    int correct = FLitem.getCorrectAnswersInt();
                    correct++;
                    FLitem.setCorrectAnswersInt(correct);
                }
                else {
                    int wrong = FLitem.getWrongAnswersInt();
                    wrong++;
                    FLitem.setWrongAnswersInt(wrong);

                }

                Button button = (Button)view;
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        loadQuestion();
                    }
                });
            }
        });

        itemCount++;
    }

}
