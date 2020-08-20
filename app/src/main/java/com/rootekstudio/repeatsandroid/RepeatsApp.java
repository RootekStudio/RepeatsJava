package com.rootekstudio.repeatsandroid;

import android.app.Application;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;

public class RepeatsApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AppCenter.start(this, "347cfec3-4ebc-443c-a9d6-4fdd34df27dd",
                Analytics.class, Crashes.class);
    }
}