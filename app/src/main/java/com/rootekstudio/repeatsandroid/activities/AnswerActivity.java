package com.rootekstudio.repeatsandroid.activities;

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

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.rootekstudio.repeatsandroid.CheckAnswer;
import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.database.SingleItemInfo;
import com.rootekstudio.repeatsandroid.notifications.NotificationHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class AnswerActivity extends AppCompatActivity {
    static String correct;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        Intent intent = getIntent();
        String Title = intent.getStringExtra("Title");
        String Question = intent.getStringExtra("Question");
        String Image = intent.getStringExtra("Image");
        correct = intent.getStringExtra("Correct");
        String IgnoreChars = intent.getStringExtra("IgnoreChars");
        String jsonIndex = intent.getStringExtra("jsonIndex");

        alarmDialog(Title, Question, getString(R.string.Check), true, Image, IgnoreChars, jsonIndex);
    }

    void alarmDialog(String Title, String Message, String Positive, boolean First, String ImageName, String ignoreChars, final String jsonIndex) {
        String buttonP = Positive;
        if (!First && Positive.equals(getString(R.string.Check))) {
            SingleItemInfo singleItemInfo;
            if(jsonIndex != null) {
                ArrayList<String> setsID = NotificationHelper.getSelectedSetsIdFromJSON(context, jsonIndex);
                singleItemInfo = new SingleItemInfo(context, setsID);
            }
            else {
                singleItemInfo = new SingleItemInfo(context);
            }

            if(singleItemInfo.getTitle() != null) {
                Title = singleItemInfo.getTitle();
                Message = singleItemInfo.getQuestion();
                correct = singleItemInfo.getAnswer();
                ImageName = singleItemInfo.getPictureName();
                ignoreChars = singleItemInfo.getIgnoreChars();
            }
            else {
                Title = context.getString(R.string.cantLoadSet);
                Message = context.getString(R.string.checkSetSettings);
                Positive = context.getString(R.string.Settings);
                correct = "";
                ImageName = "";
                ignoreChars = "";
                buttonP = "error";
            }
        }

        final String positiveButton = buttonP;

        final String IGNORE = ignoreChars;

        final MaterialAlertDialogBuilder alertDialog = new MaterialAlertDialogBuilder(this);
        alertDialog.setBackground(context.getDrawable(R.drawable.dialog_shape));

        View view = getLayoutInflater().inflate(R.layout.ask, null);
        final EditText userAnswer = view.findViewById(R.id.EditAsk);
        userAnswer.setHint(R.string.ReplyText);
        userAnswer.requestFocus();

        if (!ImageName.equals("")) {
            final ImageView imgView = view.findViewById(R.id.imageViewQuestion);
            imgView.setVisibility(View.VISIBLE);

            File file = new File(getFilesDir(), ImageName);
            FileInputStream inputStream = null;
            try {
                inputStream = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            imgView.setImageBitmap(bitmap);
        }

        if (Positive.equals(getString(R.string.Check))) {
            alertDialog.setView(view);
        }

        alertDialog.setTitle(Title)
                .setMessage(Message)
                .setCancelable(false)
                .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })

                .setPositiveButton(Positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (positiveButton.equals(getString(R.string.Check))) {
                            String uAnswerString = userAnswer.getText().toString();
                            String ReallyCorrect = correct;

                            boolean check = CheckAnswer.isAnswerCorrect(uAnswerString, ReallyCorrect, IGNORE);

                            if(check) {
                                dialog.dismiss();
                                alarmDialog(getString(R.string.CorrectAnswer1), getString(R.string.CorrectAnswer2), getString(R.string.Next), false, "", "", jsonIndex);
                            }
                            else {
                                dialog.dismiss();
                                alarmDialog(getString(R.string.IncorrectAnswer1), getString(R.string.IncorrectAnswer2) + " " + ReallyCorrect, getString(R.string.Next), false, "", "", jsonIndex);
                            }

                        } else if(positiveButton.equals("error")){
                            Intent intent = new Intent(context, SettingsActivity.class);
                            context.startActivity(intent);
                        }
                        else {
                            dialog.dismiss();
                            alarmDialog(getString(R.string.CorrectAnswer1), getString(R.string.CorrectAnswer2), getString(R.string.Check), false, "", "", jsonIndex);
                        }
                    }
                });

                AlertDialog dialog = alertDialog.create();
                dialog.show();
    }
}
