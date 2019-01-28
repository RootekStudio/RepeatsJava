package com.rootekstudio.repeatsandroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class SettingsActivity extends AppCompatActivity
{
    static FragmentActivity activity;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        activity = this;

        RepeatsHelper.DarkTheme(this);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new Preference_Screen())
                .commit();
    }
}
