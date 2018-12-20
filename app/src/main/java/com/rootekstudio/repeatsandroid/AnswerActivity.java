package com.rootekstudio.repeatsandroid;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

import javax.sql.ConnectionPoolDataSource;

public class AnswerActivity extends AppCompatActivity
{
    static String correct;
    static Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        context = this;
        Intent intent = getIntent();
        String Title = intent.getStringExtra("Title");
        String Question = intent.getStringExtra("Question");
        correct = intent.getStringExtra("Correct");

        alarmDialog(Title, Question, getString(R.string.Check), true);
    }

    void alarmDialog(String Title, String Message, final String Positive, boolean First)
    {
        if(!First && Positive.equals(getString(R.string.Check)))
        {
            RepeatsHelper.GetQuestionFromDatabase(context);
            Title = RepeatsHelper.tablename;
            Message = RepeatsHelper.Question;
            correct = RepeatsHelper.Answer;
        }

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        View view = getLayoutInflater().inflate(R.layout.ask, null);
        final EditText userAnswer = view.findViewById(R.id.EditAsk);

        if(Positive.equals(getString(R.string.Check)))
        {
            alertDialog.setView(view);
        }

        alertDialog.setTitle(Title)
                .setMessage(Message)
                .setCancelable(false)
                .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        finish();
                    }
                })

                .setPositiveButton(Positive, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if(Positive.equals(getString(R.string.Check)))
                        {
                            String uAnswerString = userAnswer.getText().toString();

                            if(correct.equals(uAnswerString))
                            {
                                dialog.dismiss();
                                alarmDialog(getString(R.string.CorrectAnswer1), getString(R.string.CorrectAnswer2), getString(R.string.Next), false);
                            }
                            else
                            {
                                dialog.dismiss();
                                alarmDialog(getString(R.string.IncorrectAnswer1), getString(R.string.IncorrectAnswer2) + " " + correct, getString(R.string.Next), false);
                            }
                        }
                        else
                        {
                            dialog.dismiss();
                            alarmDialog(getString(R.string.CorrectAnswer1), getString(R.string.CorrectAnswer2), getString(R.string.Check), false);
                        }
                    }
                })
                .show();
    }
}
