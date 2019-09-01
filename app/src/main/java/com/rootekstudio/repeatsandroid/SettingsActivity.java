package com.rootekstudio.repeatsandroid;

import android.os.Bundle;
import android.view.MenuItem;

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

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == android.R.id.home)
        {
            onBackPressed();
        }
        return true;
    }



}
