package com.rootekstudio.repeatsandroid.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.preference.PreferenceManager;

import com.rootekstudio.repeatsandroid.RepeatsHelper;

import java.util.UUID;

public class SharedPreferencesManager {
    private final String FREQUENCY_KEY = "frequency";
    private final String SILENCE_HOURS_KEY = "silenceHoursSwitch";
    private final String USER_ID_KEY = "userID";
    private final String FIRST_RUN_KEY = "firstRunTerms";
    private final String LIST_NOTIFI_KEY = "ListNotifi";
    private final String VERSION_KEY = "version";
    private final String THEME_KEY = "theme";
    private final String BATTERY_OPTIMIZATION_KEY = "batteryOptimization";

    private SharedPreferences sharedPreferences;

    private static SharedPreferencesManager single_instance = null;

    private SharedPreferencesManager(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static synchronized SharedPreferencesManager getInstance(Context context) {
        if (single_instance == null) {
            single_instance = new SharedPreferencesManager(context.getApplicationContext());
        }
        return single_instance;
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public void setFrequency(int frequency) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(FREQUENCY_KEY, frequency);
        editor.apply();
    }

    public int getFrequency() {
        if (!sharedPreferences.contains(FREQUENCY_KEY)) {
            setFrequency(30);
        }
        return sharedPreferences.getInt(FREQUENCY_KEY, 30);
    }

    public void setSilenceHours(boolean silenceHours) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SILENCE_HOURS_KEY, silenceHours);
        editor.apply();
    }

    public boolean getSilenceHours() {
        if (!sharedPreferences.contains(SILENCE_HOURS_KEY)) {
            setSilenceHours(false);
        }
        return sharedPreferences.getBoolean(SILENCE_HOURS_KEY, false);
    }

    public void setUserID(String userID) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER_ID_KEY, userID);
        editor.apply();
    }

    public String getUserID() {
        if (!sharedPreferences.contains(USER_ID_KEY)) {
            String uniqueID = UUID.randomUUID().toString();
            setUserID(uniqueID);
        }
        return sharedPreferences.getString(USER_ID_KEY, "");
    }

    public void setFirstRunTerms(int firstRunTerms) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(FIRST_RUN_KEY, firstRunTerms);
        editor.apply();
    }

    public int getFirstRunTerms() {
        if (!sharedPreferences.contains(FIRST_RUN_KEY)) {
            setFirstRunTerms(2);
            return 0;
        }
        return sharedPreferences.getInt(FIRST_RUN_KEY, 2);
    }

    public void setListNotifi(String listNotifi) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LIST_NOTIFI_KEY, listNotifi);
        editor.apply();
    }

    public String getListNotifi() {
        if (sharedPreferences.contains(LIST_NOTIFI_KEY)) {
            setListNotifi("0");
        }
        return sharedPreferences.getString(LIST_NOTIFI_KEY, "0");
    }

    public void setVersion(String version) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(VERSION_KEY, version);
        editor.apply();
    }

    public String getVersion() {
        if (!sharedPreferences.contains(VERSION_KEY)) {
            setVersion(RepeatsHelper.version);
        }
        return sharedPreferences.getString(VERSION_KEY, "");
    }

    public void setTheme(String theme) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(THEME_KEY, theme);
        editor.apply();
    }

    public String getTheme() {
        if (!sharedPreferences.contains(THEME_KEY)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                setTheme("2");
            } else {
                setTheme("1");
            }
        }
        return sharedPreferences.getString(THEME_KEY, "1");
    }

    public void setBatteryOptimization(boolean batteryOptimization) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(BATTERY_OPTIMIZATION_KEY, batteryOptimization);
        editor.apply();
    }

    public boolean getBatteryOptimization() {
        if(!sharedPreferences.contains(BATTERY_OPTIMIZATION_KEY)) {
            setBatteryOptimization(false);
        }
        return sharedPreferences.getBoolean(BATTERY_OPTIMIZATION_KEY, false);
    }
}
