package com.rootekstudio.repeatsandroid;

import android.app.RemoteInput;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
public class UserReply extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String UserAnswer = getMessageText(intent).toString();
        String Correct = intent.getStringExtra("Correct");

        String ReallyCorrect = Correct;
        String IgnoreChars = intent.getStringExtra("IgnoreChars");

        boolean check = CheckAnswer.isAnswerCorrect(UserAnswer, Correct, IgnoreChars);

        if(check) {
            RepeatsNotificationTemplate.AnswerNotifi(context,
                    context.getString(R.string.CorrectAnswer1),
                    context.getString(R.string.CorrectAnswer2));
        }
        else {
            RepeatsNotificationTemplate.AnswerNotifi(context,
                    context.getString(R.string.IncorrectAnswer1),
                    context.getString(R.string.IncorrectAnswer2) + " " + ReallyCorrect);
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
