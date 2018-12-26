package com.rootekstudio.repeatsandroid;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.MultiSelectListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreferenceCompat;

public class Preference_Screen extends PreferenceFragmentCompat
{
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
    {
        final Context context = getPreferenceManager().getContext();
        PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(context);

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        int freq = sharedPreferences.getInt("frequency", 0);

        sharedPreferences.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener()
        {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
            {
                if(key.equals("frequency"))
                {
                    int frequency = sharedPreferences.getInt("frequency", 0);
                    findPreference("timeAsk").setSummary(getString(R.string.FreqText) + " " + frequency + " " + getString(R.string.minutes));
                }
            }
        });

        SwitchPreferenceCompat notificationPreference = new SwitchPreferenceCompat(context);
        notificationPreference.setKey("notifications");
        notificationPreference.setTitle(R.string.notifications);
        notificationPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
        {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue)
            {
                if((boolean)newValue)
                {
                    RepeatsHelper.RegisterNotifications(context);
                    findPreference("timeAsk").setVisible(true);
                    findPreference("EnableSets").setVisible(true);
                    findPreference("reset").setVisible(true);

                }
                else
                {
                    RepeatsHelper.CancelNotifications(context);
                    findPreference("timeAsk").setVisible(false);
                    findPreference("EnableSets").setVisible(false);
                    findPreference("reset").setVisible(false);
                }
                return true;
            }
        });

        Preference timeAsk = new Preference(context);
        timeAsk.setKey("timeAsk");
        timeAsk.setTitle(R.string.FreqAskPref);
        timeAsk.setSummary(getString(R.string.FreqText) + " " + freq + " " + getString(R.string.minutes));
        timeAsk.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            @Override
            public boolean onPreferenceClick(Preference preference)
            {
                RepeatsHelper.AskAboutTime(context, false);
                return true;
            }
        });

        Preference enableSets = new Preference(context);
        enableSets.setKey("EnableSets");
        enableSets.setTitle(R.string.EnableTitle);
        enableSets.setSummary(R.string.EnableSummary);
        enableSets.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            @Override
            public boolean onPreferenceClick(Preference preference)
            {
                Intent intent = new Intent(context, EnableSetsList.class);
                startActivity(intent);
                return true;
            }
        });

        Preference reset = new Preference(context);
        reset.setKey("reset");
        reset.setTitle(R.string.resetTitle);
        reset.setSummary(R.string.resetSummary);
        reset.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            @Override
            public boolean onPreferenceClick(Preference preference)
            {
                DatabaseHelper DB = new DatabaseHelper(context);
                DB.ResetEnabled();

                RepeatsHelper.SaveFrequency(context, 5);
                RepeatsHelper.CancelNotifications(context);
                RepeatsHelper.RegisterNotifications(context);

                int frequency = sharedPreferences.getInt("frequency", 0);
                findPreference("timeAsk").setSummary(getString(R.string.FreqText) + " " + frequency + " " + getString(R.string.minutes));

                Toast.makeText(context, getString(R.string.ResetSuccessfully), Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        PreferenceCategory notification_category = new PreferenceCategory(context);
        notification_category.setKey("NotifiCat");
        notification_category.setTitle(R.string.notifications);
        screen.addPreference(notification_category);
        notification_category.addPreference(notificationPreference);
        notification_category.addPreference(timeAsk);
        notification_category.addPreference(enableSets);
        notification_category.addPreference(reset);

        final ListPreference theme = new ListPreference(context);
        theme.setKey("theme");
        theme.setTitle(R.string.changeTheme);
        theme.setEntries(R.array.themes);
        theme.setEntryValues(R.array.ThemeValues);
        theme.setValue("0");
        theme.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
        {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue)
            {
                String value = (String)newValue;
                if(value.equals("1"))
                {
                    getActivity().finish();
                    Intent intent = new Intent(context, SettingsActivity.class);
                    startActivity(intent);
                }
                else
                {
                    getActivity().finish();
                    Intent intent = new Intent(context, SettingsActivity.class);
                    startActivity(intent);
                }
                return true;
            }
        });

        PreferenceCategory theme_cat = new PreferenceCategory(context);
        theme_cat.setKey("theme_cat");
        theme_cat.setTitle(R.string.Theme);
        screen.addPreference(theme_cat);
        theme_cat.addPreference(theme);

        setPreferenceScreen(screen);

        boolean notifiEnabled = sharedPreferences.getBoolean("notifications", false);

        if(notifiEnabled)
        {
            findPreference("timeAsk").setVisible(true);
            findPreference("EnableSets").setVisible(true);
            findPreference("reset").setVisible(true);
        }
        else
        {
            findPreference("timeAsk").setVisible(false);
            findPreference("EnableSets").setVisible(false);
            findPreference("reset").setVisible(false);
        }
    }

    void AskTimeAlarm()
    {
        final Context cnt = getContext();


    }
}
