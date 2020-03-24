package com.rootekstudio.repeatsandroid.activities;

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
import com.rootekstudio.repeatsandroid.JsonFile;
import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.RepeatsHelper;
import com.rootekstudio.repeatsandroid.database.DatabaseHelper;
import com.rootekstudio.repeatsandroid.database.GetQuestion;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class AnswerActivity extends AppCompatActivity {
    GetQuestion getQuestion;
    String jsonIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        RepeatsHelper.DarkTheme(this, false);
        super.onCreate(savedInstanceState);
        getQuestion = new GetQuestion();
        Intent intent = getIntent();

        getQuestion.setTitle(intent.getStringExtra("Title"));
        getQuestion.setQuestion(intent.getStringExtra("Question"));
        getQuestion.setPictureName(intent.getStringExtra("Image"));
        getQuestion.setAnswer(intent.getStringExtra("Correct"));
        getQuestion.setIgnoreChars(intent.getStringExtra("IgnoreChars"));
        getQuestion.setSetID(intent.getStringExtra("setID"));
        getQuestion.setItemID(intent.getIntExtra("itemID", -1));
        jsonIndex = intent.getStringExtra("jsonIndex");

        createAlertDialogWithQuestion();
    }

    private void createAlertDialogWithQuestion() {
        String title = getQuestion.getTitle();
        String message = getQuestion.getQuestion();
        String image = getQuestion.getPictureName();

        MaterialAlertDialogBuilder alertDialog = new MaterialAlertDialogBuilder(this);
        alertDialog.setBackground(getDrawable(R.drawable.dialog_shape));

        View view = getLayoutInflater().inflate(R.layout.ask, null);
        final EditText answerEditText = view.findViewById(R.id.EditAsk);
        answerEditText.setHint(R.string.ReplyText);
        answerEditText.requestFocus();

        if (!image.equals("")) {
            final ImageView imgView = view.findViewById(R.id.imageViewQuestion);
            imgView.setVisibility(View.VISIBLE);

            File file = new File(getFilesDir(), image);
            FileInputStream inputStream = null;
            try {
                inputStream = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            imgView.setImageBitmap(bitmap);
        }
        alertDialog.setTitle(title)
                .setMessage(message)
                .setView(view)
                .setCancelable(false)
                .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .setPositiveButton(R.string.Check, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String userAnswer = answerEditText.getText().toString();
                        String correctAnswer = getQuestion.getAnswer();

                        if (CheckAnswer.isAnswerCorrect(userAnswer, correctAnswer, getQuestion.getIgnoreChars())) {
                            if (correctAnswer.contains("\n")) {
                                correctAnswer = correctAnswer.replace("\r\n", ", ");
                                dialogInterface.dismiss();
                                createAlertDialogWithAnswer(getString(R.string.CorrectAnswer1), getString(R.string.CorrectAnswer2) + "\n" +
                                        getString(R.string.otherCorrectAnswers) + " " + correctAnswer);
                            } else {
                                dialogInterface.dismiss();
                                createAlertDialogWithAnswer(getString(R.string.CorrectAnswer1), getString(R.string.CorrectAnswer2));
                            }

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    String setID = getQuestion.getSetID();
                                    int itemID = getQuestion.getItemID();

                                    DatabaseHelper DB = new DatabaseHelper(AnswerActivity.this);
                                    DB.increaseValueInSet(setID, itemID, "goodAnswers", 1);
                                    DB.increaseValueInSet(setID, itemID, "allAnswers", 1);
                                    DB.increaseValueInTitleTable(setID, "goodAnswers", 1);
                                    DB.increaseValueInTitleTable(setID, "allAnswers", 1);
                                }
                            }).start();

                        } else {
                            dialogInterface.dismiss();
                            createAlertDialogWithAnswer(getString(R.string.IncorrectAnswer1), getString(R.string.IncorrectAnswer2) + " " + correctAnswer);

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    String setID = getQuestion.getSetID();
                                    int itemID = getQuestion.getItemID();

                                    DatabaseHelper DB = new DatabaseHelper(AnswerActivity.this);
                                    DB.increaseValueInSet(setID, itemID, "wrongAnswers", 1);
                                    DB.increaseValueInSet(setID, itemID, "allAnswers", 1);
                                    DB.increaseValueInTitleTable(setID, "wrongAnswers", 1);
                                    DB.increaseValueInTitleTable(setID, "allAnswers", 1);
                                }
                            }).start();
                        }
                    }
                });

        AlertDialog dialog = alertDialog.create();
        dialog.show();
    }

    private void createAlertDialogWithAnswer(String title, String message) {
        MaterialAlertDialogBuilder alertDialog = new MaterialAlertDialogBuilder(this);
        alertDialog.setBackground(getDrawable(R.drawable.dialog_shape))
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .setPositiveButton(R.string.Next, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (jsonIndex != null) {
                            ArrayList<String> setsID = JsonFile.getSelectedSetsIdFromJSON(AnswerActivity.this, jsonIndex);
                            getQuestion = new GetQuestion(AnswerActivity.this, setsID);
                        } else {
                            getQuestion = new GetQuestion(AnswerActivity.this);
                        }
                        dialogInterface.dismiss();
                        if (getQuestion.getQuestion() == null) {
                            createAlertDialogWithError();
                        } else {
                            createAlertDialogWithQuestion();
                        }
                    }
                });

        AlertDialog dialog = alertDialog.create();
        dialog.show();
    }

    private void createAlertDialogWithError() {
        MaterialAlertDialogBuilder alertDialog = new MaterialAlertDialogBuilder(this);
        alertDialog.setBackground(getDrawable(R.drawable.dialog_shape))
                .setTitle(getString(R.string.cantLoadSet))
                .setMessage(getString(R.string.checkSetSettings))
                .setCancelable(false)
                .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .setPositiveButton(R.string.Settings, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(AnswerActivity.this, SettingsActivity.class);
                        startActivity(intent);

                        finish();
                    }
                });

        AlertDialog dialog = alertDialog.create();
        dialog.show();
    }

}
