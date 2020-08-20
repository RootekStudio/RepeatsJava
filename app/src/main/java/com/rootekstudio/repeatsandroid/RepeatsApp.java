package com.rootekstudio.repeatsandroid;

import android.app.Application;

public class RepeatsApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        RepeatsAnalytics.startAnalytics(this);
    }
}