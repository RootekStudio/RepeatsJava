package com.rootekstudio.repeatsandroid.notifications;

import android.app.RemoteInput;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.rootekstudio.repeatsandroid.CheckAnswer;
import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.database.RepeatsDatabase;
import com.rootekstudio.repeatsandroid.database.Values;

public class UserReply extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String UserAnswer = getMessageText(intent).toString();
        String setID = intent.getStringExtra("setID");
        int itemID = intent.getIntExtra("itemID", -1);
        String setsIDs = intent.getStringExtra("setsIDs");
        String Correct = intent.getStringExtra("Correct");

        String ReallyCorrect = Correct;
        int IgnoreChars = intent.getIntExtra("IgnoreChars", 0);
        boolean ignoreChars = false;
        if(IgnoreChars == 1) {
            ignoreChars = true;
        }

        RepeatsDatabase DB = RepeatsDatabase.getInstance(context);
        boolean check = CheckAnswer.isAnswerCorrect(UserAnswer, Correct, ignoreChars);

        if (check) {
            if (ReallyCorrect.contains("\n")) {
                ReallyCorrect = ReallyCorrect.replace("\r\n", ", ");
                RepeatsNotificationTemplate.AnswerNotifi(context,
                        context.getString(R.string.CorrectAnswer1),
                        context.getString(R.string.CorrectAnswer2) + "\n" + context.getString(R.string.allCorrectAnswers) + " " + ReallyCorrect, setsIDs);
            } else {
                RepeatsNotificationTemplate.AnswerNotifi(context,
                        context.getString(R.string.CorrectAnswer1),
                        context.getString(R.string.CorrectAnswer2), setsIDs);
            }

            new Thread(() -> {
                DB.increaseValueInSet(setID, itemID, Values.good_answers, 1);
                DB.increaseValueInSetsInfo(setID, Values.good_answers, 1);
            }).start();
        } else {
            if(ReallyCorrect.contains("\n")) {
                ReallyCorrect = ReallyCorrect.replace("\r\n", ", ");
                RepeatsNotificationTemplate.AnswerNotifi(context,
                        context.getString(R.string.IncorrectAnswer1),
                        context.getString(R.string.correct_answers) + " " + ReallyCorrect, setsIDs);
            } else {
                RepeatsNotificationTemplate.AnswerNotifi(context,
                        context.getString(R.string.IncorrectAnswer1),
                        context.getString(R.string.IncorrectAnswer2) + " " + ReallyCorrect, setsIDs);
            }


            new Thread(() -> {
                DB.increaseValueInSet(setID, itemID, Values.wrong_answers, 1);
                DB.increaseValueInSetsInfo(setID, Values.wrong_answers, 1);
            }).start();
        }
    }

    private CharSequence getMessageText(Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            return remoteInput.getCharSequence("UsersAnswer");
        }
        return null;
    }
}
