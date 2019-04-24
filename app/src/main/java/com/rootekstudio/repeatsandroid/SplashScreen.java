package com.rootekstudio.repeatsandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class SplashScreen extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        RepeatsHelper.DarkTheme(this);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
