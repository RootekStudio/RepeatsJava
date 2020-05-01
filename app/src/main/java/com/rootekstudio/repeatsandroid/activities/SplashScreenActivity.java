package com.rootekstudio.repeatsandroid.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.rootekstudio.repeatsandroid.RepeatsHelper;
import com.rootekstudio.repeatsandroid.firstrun.FirstRunActivity;
import com.rootekstudio.repeatsandroid.mainpage.MainActivity;

import java.util.UUID;

import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;

public class SplashScreenActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        RepeatsHelper.DarkTheme(this, false);

        super.onCreate(savedInstanceState);

        Intent intent;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (!sharedPreferences.contains("firstRunTerms")) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("firstRunTerms", 2);
            editor.apply();

            intent = new Intent(this, FirstRunActivity.class);
        } else {
            intent = new Intent(this, MainActivity.class);
        }

        if (!sharedPreferences.contains("userID")) {
            String uniqueID = UUID.randomUUID().toString();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("userID", uniqueID);
            editor.apply();
        }
//
//        AppCenter.start(getApplication(), "347cfec3-4ebc-443c-a9d6-4fdd34df27dd",
//                Analytics.class, Crashes.class);

        startActivity(intent);
        finish();
    }
}
