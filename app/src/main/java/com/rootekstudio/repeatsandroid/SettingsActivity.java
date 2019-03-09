package com.rootekstudio.repeatsandroid;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

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
