package com.rootekstudio.repeatsandroid.fastlearning;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rootekstudio.repeatsandroid.CheckAnswer;
import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.RepeatsSingleItem;
import com.rootekstudio.repeatsandroid.database.DatabaseHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class FastLearningActivity extends AppCompatActivity {
    TextView setNameTxtView;
    TextView question;
    EditText userAnswer;
    TextView correctAnswer;
    TextView otherCorrectAnswers;
    LinearLayout correctAnswerLinear;
    LinearLayout otherCorrectLinear;
    ProgressBar progressBar;
    TextView goodAnswersCountTxtView;
    TextView wrongAnswersCountTxtView;
    TextView allAnswersCountTxtView;
    TextView percentTxtView;
    TextView countTimeTxtView;
    ImageView imageView;
    ScrollView scrollView;

    int goodAnswersCount;
    int wrongAnswersCount;
    int allAnswersCount;

    int allQuestionsCount;
    int itemCount;
    String goodAnswer;

    int seconds;
    int minutes;
    String secondsString;
    String minutesString;
    String time;
    Timer timer;

    ColorStateList oldTextColor;
    List<RepeatsSingleItem> fastLearningItemList;
    RepeatsSingleItem singleItem;

    DatabaseHelper DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fast_learning);
        getSupportActionBar().hide();

        setNameTxtView = findViewById(R.id.setNameFastLearning);
        question = findViewById(R.id.questionTextViewFL);
        userAnswer = findViewById(R.id.userAnswerTextViewFL);
        correctAnswer = findViewById(R.id.correctAnswerTextViewFL);
        otherCorrectAnswers = findViewById(R.id.otherCorrectAnswersTextViewFL);
        correctAnswerLinear = findViewById(R.id.linearGoodAnswer);
        otherCorrectLinear = findViewById(R.id.linearOtherAnswers);
        progressBar = findViewById(R.id.progressBarFastLearning);
        goodAnswersCountTxtView = findViewById(R.id.goodAnswersCountFL);
        wrongAnswersCountTxtView = findViewById(R.id.wrongAnswersCountFL);
        allAnswersCountTxtView = findViewById(R.id.allAnswersCountFL);
        percentTxtView = findViewById(R.id.percentFastLearning);
        countTimeTxtView = findViewById(R.id.fastLearningTime);
        imageView = findViewById(R.id.imageViewFastLearning);
        scrollView = findViewById(R.id.scrollViewFastLearning);

        DB = new DatabaseHelper(this);

        itemCount = -1;
        goodAnswersCount = 0;
        wrongAnswersCount = 0;
        allAnswersCount = 0;
        goodAnswer = "";
        percentTxtView.setText("0%");
        allQuestionsCount = FastLearningInfo.questionsCount;
        progressBar.setMax(allQuestionsCount);
        progressBar.setProgress(0);
        fastLearningItemList = FastLearningInfo.selectedQuestions;

        goodAnswersCountTxtView.setText("+\n0");
        wrongAnswersCountTxtView.setText("-\n0");
        allAnswersCountTxtView.setText("=\n0");

        oldTextColor = userAnswer.getTextColors();

        loadQuestion();
        setTimer();
    }

    private void loadQuestion() {
        itemCount++;

        singleItem = fastLearningItemList.get(itemCount);
        String setString = getString(R.string.Set) + ":\n" + singleItem.getSetName();
        setNameTxtView.setText(setString);
        question.setText(singleItem.getQuestion());
        userAnswer.setText("");

        correctAnswerLinear.setVisibility(View.GONE);
        otherCorrectLinear.setVisibility(View.GONE);
        userAnswer.setEnabled(true);
        userAnswer.setTextColor(oldTextColor);

        goodAnswer = singleItem.getAnswer();
        if (goodAnswer.contains("\n")) {
            String firstCorrect = goodAnswer.substring(0, goodAnswer.indexOf("\r\n"));
            correctAnswer.setText(firstCorrect);
            String anotherCorrects = goodAnswer.replace("\r\n", ", ");
            otherCorrectAnswers.setText(anotherCorrects);
        } else {
            correctAnswer.setText(goodAnswer);
        }

        String imageName = singleItem.getImag();
        if (!imageName.equals("")) {
            imageView.setVisibility(View.VISIBLE);

            File file = new File(getFilesDir(), imageName);
            FileInputStream inputStream = null;
            try {
                inputStream = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            imageView.setImageBitmap(bitmap);

            try {
                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            imageView.setImageBitmap(null);
            imageView.setVisibility(View.GONE);
        }
    }

    public void fabClick(View view) {
        FloatingActionButton fab = (FloatingActionButton) view;
        if (fab.getBackgroundTintList() == ColorStateList.valueOf(getResources().getColor(R.color.greenRepeats))) {

            String uAnswer = userAnswer.getText().toString();
            userAnswer.setEnabled(false);
            allAnswersCount++;

            if (CheckAnswer.isAnswerCorrect(uAnswer, goodAnswer, String.valueOf(FastLearningInfo.ignoreChars))) {
                if (goodAnswer.contains("\n")) {
                    otherCorrectLinear.setVisibility(View.VISIBLE);
                }

                userAnswer.setTextColor(getResources().getColor(R.color.greenRepeats));
                goodAnswersCount++;

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        DB.increaseValueInSet(singleItem.getSetID(), singleItem.getItemID(), "goodAnswers", 1);
                        DB.increaseValueInSet(singleItem.getSetID(), singleItem.getItemID(), "allAnswers", 1);
                        DB.increaseValueInTitleTable(singleItem.getSetID(), "goodAnswers", 1);
                        DB.increaseValueInTitleTable(singleItem.getSetID(), "allAnswers", 1);
                    }
                }).start();

            } else {
                correctAnswerLinear.setVisibility(View.VISIBLE);
                if (goodAnswer.contains("\n")) {
                    otherCorrectLinear.setVisibility(View.VISIBLE);
                }

                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.scrollTo(0, scrollView.getBottom());
                    }
                });

                if (userAnswer.getText().toString().equals("")) {
                    userAnswer.setText(getString(R.string.noAnswer));
                }
                userAnswer.setTextColor(getResources().getColor(R.color.redRepeats));
                wrongAnswersCount++;

                if (allAnswersCount != fastLearningItemList.size()) {
                    Random random = new Random();
                    int randomIndex = random.nextInt((fastLearningItemList.size() - allAnswersCount) + 1) + allAnswersCount;
                    fastLearningItemList.add(randomIndex, singleItem);
                } else {
                    fastLearningItemList.add(singleItem);
                }

                allQuestionsCount++;

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        DB.increaseValueInSet(singleItem.getSetID(), singleItem.getItemID(), "wrongAnswers", 1);
                        DB.increaseValueInSet(singleItem.getSetID(), singleItem.getItemID(), "allAnswers", 1);
                        DB.increaseValueInTitleTable(singleItem.getSetID(), "wrongAnswers", 1);
                        DB.increaseValueInTitleTable(singleItem.getSetID(), "allAnswers", 1);
                    }
                }).start();
            }

            String goodAnswersCountText = "+\n" + goodAnswersCount;
            String wrongAnswersCountText = "-\n" + wrongAnswersCount;
            String allAnswersCountText = "=\n" + allAnswersCount;

            goodAnswersCountTxtView.setText(goodAnswersCountText);
            wrongAnswersCountTxtView.setText(wrongAnswersCountText);
            allAnswersCountTxtView.setText(allAnswersCountText);

            progressBar.setMax(FastLearningInfo.questionsCount);
            progressBar.setProgress(goodAnswersCount);

            String percent = Math.round((float) goodAnswersCount / FastLearningInfo.questionsCount * 100) + "%";
            percentTxtView.setText(percent);

            if(allAnswersCount == fastLearningItemList.size()) {
                timer.cancel();
                String message = getString(R.string.in) +" " + minutes + " " + getString(R.string.minutes) + " " +
                        seconds + " " + getString(R.string.seconds) + " " + getString(R.string.youLearned) + " " +
                        FastLearningInfo.questionsCount + " " + getString(R.string.words) + ".";

                MaterialAlertDialogBuilder alertDialog = new MaterialAlertDialogBuilder(this);
                alertDialog.setBackground(getDrawable(R.drawable.dialog_shape))
                        .setTitle(R.string.FLendText)
                        .setMessage(message)
                        .setCancelable(false)
                        .setNegativeButton(R.string.OnceAgain, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(getApplicationContext(), FastLearningActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setPositiveButton(R.string.exit, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        });

                AlertDialog dialog = alertDialog.create();
                dialog.show();
            }

            fab.setImageDrawable(getDrawable(R.drawable.ic_navigate_next));
            fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
        } else {
            loadQuestion();
            fab.setImageDrawable(getDrawable(R.drawable.ic_check));
            fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.greenRepeats)));
        }
    }

    public void exitClick(View view) {
        askToExit();
    }

    @Override
    public void onBackPressed() {
        askToExit();
    }

    private void askToExit() {
        MaterialAlertDialogBuilder alertDialog = new MaterialAlertDialogBuilder(this);
        alertDialog.setBackground(getDrawable(R.drawable.dialog_shape))
                .setTitle(R.string.finishFastLearning)
                .setMessage(R.string.finishFastLearningQuestion)
                .setNegativeButton(R.string.Cancel, null)
                .setPositiveButton(R.string.exit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });

        AlertDialog dialog = alertDialog.create();
        dialog.show();
    }

    private void setTimer() {
        seconds = -1;
        minutes = 0;
        secondsString = "00";
        minutesString = "00";
        time = getString(R.string.time);

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                seconds++;
                if (seconds <= 9) {
                    secondsString = "0" + seconds;
                } else if (seconds < 60) {
                    secondsString = String.valueOf(seconds);
                } else {
                    minutes++;
                    seconds = 0;
                    secondsString = "00";

                    if (minutes <= 9) {
                        minutesString = "0" + minutes;
                    } else {
                        minutesString = String.valueOf(minutes);
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String timeString = time + ": " + minutesString + ":" + secondsString;
                        countTimeTxtView.setText(timeString);
                    }
                });
            }
        }, 0, 1000);
    }
}
