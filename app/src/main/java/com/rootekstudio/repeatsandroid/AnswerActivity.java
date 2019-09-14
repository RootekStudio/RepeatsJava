package com.rootekstudio.repeatsandroid;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.Normalizer;
import java.util.Locale;

public class AnswerActivity extends AppCompatActivity
{
    static String correct;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        context = this;
        Intent intent = getIntent();
        String Title = intent.getStringExtra("Title");
        String Question = intent.getStringExtra("Question");
        String Image = intent.getStringExtra("Image");
        correct = intent.getStringExtra("Correct");
        String IgnoreChars = intent.getStringExtra("IgnoreChars");

        alarmDialog(Title, Question, getString(R.string.Check), true, Image, IgnoreChars);
    }

    void alarmDialog(String Title, String Message, final String Positive, boolean First, String ImageName, String ignoreChars)
    {
        if(!First && Positive.equals(getString(R.string.Check)))
        {
            RepeatsHelper.GetQuestionFromDatabase(context);
            Title = RepeatsHelper.tablename;
            Message = RepeatsHelper.Question;
            correct = RepeatsHelper.Answer;
            ImageName = RepeatsHelper.PictureName;
            ignoreChars = RepeatsHelper.IgnoreChars;
        }

        final String IGNORE = ignoreChars;

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        View view = getLayoutInflater().inflate(R.layout.ask, null);
        final EditText userAnswer = view.findViewById(R.id.EditAsk);
        userAnswer.setHint(R.string.ReplyText);
        userAnswer.requestFocus();

        if(!ImageName.equals(""))
        {
            final ImageView imgView = view.findViewById(R.id.imageViewQuestion);
            imgView.setVisibility(View.VISIBLE);

            File file = new File(getFilesDir(), ImageName);
            FileInputStream inputStream = null;
            try
            {
                inputStream = new FileInputStream(file);
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }

            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            imgView.setImageBitmap(bitmap);
        }

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
                            String ReallyCorrect = correct;

                            if(IGNORE.equals("true"))
                            {
                                uAnswerString = Normalizer.normalize(uAnswerString, Normalizer.Form.NFD)
                                        .replaceAll(" ", "")
                                        .replaceAll("Ł","l")
                                        .replaceAll("ł", "l")
                                        .replaceAll("[^\\p{ASCII}]", "")
                                        .toLowerCase(Locale.getDefault());

                                correct = Normalizer.normalize(correct, Normalizer.Form.NFD)
                                        .replaceAll(" ", "")
                                        .replaceAll("Ł","l")
                                        .replaceAll("ł", "l")
                                        .replaceAll("[^\\p{ASCII}]", "")
                                        .toLowerCase(Locale.getDefault());
                            }

                            if(correct.equals(uAnswerString))
                            {
                                dialog.dismiss();
                                alarmDialog(getString(R.string.CorrectAnswer1), getString(R.string.CorrectAnswer2), getString(R.string.Next), false, "", "");
                            }
                            else
                            {
                                dialog.dismiss();
                                alarmDialog(getString(R.string.IncorrectAnswer1), getString(R.string.IncorrectAnswer2) + " " + ReallyCorrect, getString(R.string.Next), false, "","");
                            }
                        }
                        else
                        {
                            dialog.dismiss();
                            alarmDialog(getString(R.string.CorrectAnswer1), getString(R.string.CorrectAnswer2), getString(R.string.Check), false, "", "");
                        }
                    }
                })
                .show();
    }
}
