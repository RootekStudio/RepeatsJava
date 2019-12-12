package com.rootekstudio.repeatsandroid.activities;

import android.app.UiModeManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.RepeatsHelper;

public class SplashScreenActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        RepeatsHelper.DarkTheme(this, false);

        super.onCreate(savedInstanceState);

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
