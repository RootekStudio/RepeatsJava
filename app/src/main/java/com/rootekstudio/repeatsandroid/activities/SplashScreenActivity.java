package com.rootekstudio.repeatsandroid.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.rootekstudio.repeatsandroid.RepeatsHelper;

public class SplashScreenActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        RepeatsHelper.DarkTheme(this, false);

        Intent intent;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(!sharedPreferences.contains("firstRun")) {
            intent = new Intent(this, FirstRunActivity.class);
        }
        else {
            intent = new Intent(this, MainActivity.class);
        }
        startActivity(intent);
        finish();
    }
}
