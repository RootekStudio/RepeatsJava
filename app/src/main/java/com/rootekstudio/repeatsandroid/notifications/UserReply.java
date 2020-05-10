package com.rootekstudio.repeatsandroid.notifications;

import android.app.RemoteInput;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;

import com.rootekstudio.repeatsandroid.CheckAnswer;
import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.notifications.RepeatsNotificationTemplate;

@RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
public class UserReply extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String UserAnswer = getMessageText(intent).toString();
        String Correct = intent.getStringExtra("Correct");

        String ReallyCorrect = Correct;
        int IgnoreChars = intent.getIntExtra("IgnoreChars", 0);
        boolean ignoreChars = false;
        if(IgnoreChars == 1) {
            ignoreChars = true;
        }

        boolean check = CheckAnswer.isAnswerCorrect(UserAnswer, Correct, ignoreChars);

        if (check) {
            if (ReallyCorrect.contains("\n")) {
                ReallyCorrect = ReallyCorrect.replace("\r\n", ", ");
                RepeatsNotificationTemplate.AnswerNotifi(context,
                        context.getString(R.string.CorrectAnswer1),
                        context.getString(R.string.CorrectAnswer2) + "\n" + context.getString(R.string.otherCorrectAnswers) + " " + ReallyCorrect, true, intent.getStringExtra("setID"), intent.getIntExtra("itemID", -1));
            } else {
                RepeatsNotificationTemplate.AnswerNotifi(context,
                        context.getString(R.string.CorrectAnswer1),
                        context.getString(R.string.CorrectAnswer2), true, intent.getStringExtra("setID"), intent.getIntExtra("itemID", -1));
            }
        } else {
            RepeatsNotificationTemplate.AnswerNotifi(context,
                    context.getString(R.string.IncorrectAnswer1),
                    context.getString(R.string.IncorrectAnswer2) + " " + ReallyCorrect, false, intent.getStringExtra("setID"), intent.getIntExtra("itemID", -1));
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
