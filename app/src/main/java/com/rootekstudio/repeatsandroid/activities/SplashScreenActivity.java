package com.rootekstudio.repeatsandroid.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.rootekstudio.repeatsandroid.RepeatsHelper;

import java.util.UUID;

public class SplashScreenActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        RepeatsHelper.DarkTheme(this, false);

        super.onCreate(savedInstanceState);

        Intent intent;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(!sharedPreferences.contains("firstRunTerms")) {
            intent = new Intent(this, FirstRunActivity.class);
        }
        else {
            intent = new Intent(this, MainActivity.class);
        }

        if(!sharedPreferences.contains("userID")) {
            String uniqueID = UUID.randomUUID().toString();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("userID", uniqueID);
            editor.apply();
        }
        startActivity(intent);
        finish();
    }
}
