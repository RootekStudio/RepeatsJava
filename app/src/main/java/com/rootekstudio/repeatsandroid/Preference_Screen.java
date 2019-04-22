package com.rootekstudio.repeatsandroid;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import java.util.List;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreferenceCompat;

public class Preference_Screen extends PreferenceFragmentCompat
{
    private static Boolean ThemeChanged = false;


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
    {
        final Context context = getPreferenceManager().getContext();
        PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(context);

        DatabaseHelper DB = new DatabaseHelper(context);
        List<RepeatsListDB> all = DB.AllItemsLIST();

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        final int freq = sharedPreferences.getInt("frequency", 0);

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

        final SwitchPreferenceCompat notificationPreference = new SwitchPreferenceCompat(context);
        notificationPreference.setKey("notifications");
        notificationPreference.setTitle(R.string.notifications);
        if(all.size() == 0)
        {
            notificationPreference.setEnabled(false);
        }
        notificationPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
        {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue)
            {
                    if((boolean)newValue)
                    {
                        int f = sharedPreferences.getInt("frequency", 0);
                        if(f == 0)
                        {
                            RepeatsHelper.AskAboutTime(context, false, SettingsActivity.activity);
                        }
                        else
                        {
                            RepeatsHelper.RegisterNotifications(context);
                        }

                        findPreference("timeAsk").setVisible(true);
                        findPreference("EnableSets").setVisible(true);
                        findPreference("reset").setVisible(true);

                    }
                    else
                    {
                        if(freq != 0)
                        {
                            RepeatsHelper.CancelNotifications(context);
                        }

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
                RepeatsHelper.AskAboutTime(context, false, SettingsActivity.activity);
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
        theme.setValue("1");
        theme.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
        {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue)
            {
                ThemeChanged = true;

                getActivity().finish();
                Intent intent = new Intent(context, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                return true;
            }
        });

        PreferenceCategory theme_cat = new PreferenceCategory(context);
        theme_cat.setKey("theme_cat");
        theme_cat.setTitle(R.string.Theme);
        screen.addPreference(theme_cat);
        theme_cat.addPreference(theme);

        setPreferenceScreen(screen);

        PackageInfo pInfo = null;
        try
        {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }

        final String version = pInfo.versionName;

        Preference About = new Preference(context);
        About.setKey("about");
        About.setTitle(null);
        About.setSummary("Repeats " + version + "\n" + "Developer: Jakub Sieradzki");

        Preference SendFeedback = new Preference(context);
        SendFeedback.setKey("feedback");
        SendFeedback.setTitle(R.string.SendFeedback);
        SendFeedback.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            @Override
            public boolean onPreferenceClick(Preference preference)
            {
                Intent send = new Intent(Intent.ACTION_SEND);
                send.setType("plain/text");
                send.putExtra(Intent.EXTRA_EMAIL, new String[]{"rootekstudio@outlook.com"});
                send.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.FeedbackSubject)+ " " + version);

                startActivity(Intent.createChooser(send, getString(R.string.SendFeedback)));

                return true;
            }
        });

        Preference github = new Preference(context);
        github.setKey("github");
        github.setTitle(R.string.github);
        github.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            @Override
            public boolean onPreferenceClick(Preference preference)
            {
                Intent send = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/RootekStudio/RepeatsJava"));
                startActivity(send);

                return true;
            }
        });

        Preference rate = new Preference(context);
        rate.setKey("rate");
        rate.setTitle(R.string.rate);
        rate.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            @Override
            public boolean onPreferenceClick(Preference preference)
            {
                Intent send = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.rootekstudio.repeatsandroid"));
                try
                {
                    startActivity(send);
                }
                catch (ActivityNotFoundException e)
                {
                    Toast.makeText(context, R.string.storeNotFound, Toast.LENGTH_LONG).show();
                }

                return true;
            }
        });

        Preference whatsNew = new Preference(context);
        whatsNew.setKey("whatsnew");
        whatsNew.setTitle(R.string.whatsNew);
        whatsNew.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            @Override
            public boolean onPreferenceClick(Preference preference)
            {
                RepeatsHelper.whatsNew(context, true);
                return true;
            }
        });


        PreferenceCategory about_cat = new PreferenceCategory(context);
        about_cat.setKey("about_cat");
        about_cat.setTitle(R.string.About);
        screen.addPreference(about_cat);
        about_cat.addPreference(github);
        about_cat.addPreference(rate);
        about_cat.addPreference(SendFeedback);
        about_cat.addPreference(whatsNew);
        about_cat.addPreference(About);

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


    @Override
    public void onResume()
    {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener()
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
    }

    @Override
    public void onPause()
    {
        super.onPause();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener()
        {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
            {
                if(key.equals("frequency"))
                {
                    try
                    {
                        int frequency = sharedPreferences.getInt("frequency", 0);
                        findPreference("timeAsk").setSummary(getString(R.string.FreqText) + " " + frequency + " " + getString(R.string.minutes));
                    }
                    catch (Exception o) {}
                }
            }
        });
    }

}
