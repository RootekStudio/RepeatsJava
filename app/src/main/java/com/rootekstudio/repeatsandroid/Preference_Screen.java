package com.rootekstudio.repeatsandroid;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.widget.Toast;

import androidx.preference.CheckBoxPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreferenceCompat;

import com.rootekstudio.repeatsandroid.activities.ChangeDeliveryListActivity;
import com.rootekstudio.repeatsandroid.activities.EnableSetsListActivity;
import com.rootekstudio.repeatsandroid.activities.FirstRunActivity;
import com.rootekstudio.repeatsandroid.activities.SettingsActivity;
import com.rootekstudio.repeatsandroid.activities.SilenceHoursActivity;
import com.rootekstudio.repeatsandroid.database.DatabaseHelper;
import com.rootekstudio.repeatsandroid.notifications.ConstNotifiSetup;
import com.rootekstudio.repeatsandroid.notifications.NotificationHelper;
import com.rootekstudio.repeatsandroid.notifications.RegisterNotifications;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;

public class Preference_Screen extends PreferenceFragmentCompat {
    Context context;
    int cliked = 0;
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreatePreferences(final Bundle savedInstanceState, String rootKey) {
        context = getPreferenceManager().getContext();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String packageName = context.getPackageName();
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            SharedPreferences.Editor edit = sharedPreferences.edit();
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                edit.putBoolean("batteryOptimization", false);
                edit.apply();
            } else {
                edit.putBoolean("batteryOptimization", true);
                edit.apply();
            }
        }

        PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(context);
        DatabaseHelper DB = new DatabaseHelper(context);
        List<RepeatsListDB> all = DB.AllItemsLIST();

        final int freq = sharedPreferences.getInt("frequency", 0);

        final ListPreference notifiListPreference = new ListPreference(context);
        notifiListPreference.setIconSpaceReserved(false);
        notifiListPreference.setKey("ListNotifi");
        notifiListPreference.setTitle(R.string.notifi_mode);
        notifiListPreference.setEntries(R.array.notifications);
        notifiListPreference.setEntryValues(R.array.notificationsValue);
        notifiListPreference.setValue("0");

        notifiListPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                int value = Integer.parseInt((String)newValue);

                if (value == 0) {
                    ConstNotifiSetup.CancelNotifications(context);

                    findPreference("timeAsk").setVisible(false);
                    findPreference("EnableSets").setVisible(false);
                    findPreference("silenceHoursSwitch").setVisible(false);
                    findPreference("silenceHoursSettings").setVisible(false);
                    findPreference("advancedDelivery").setVisible(false);

                    notifiListPreference.setSummary(R.string.turned_off);

                    try {
                        JSONObject advancedFile = new JSONObject(JsonFile.readJson(context, "advancedDelivery.json"));

                        Iterator<String> iterator = advancedFile.keys();

                        while(iterator.hasNext()) {
                            String key = iterator.next();
                            NotificationHelper.cancelAdvancedAlarm(context, Integer.parseInt(key));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else if (value == 1) {

                    ConstNotifiSetup.RegisterNotifications(context,null, RepeatsHelper.staticFrequencyCode);

                    findPreference("timeAsk").setVisible(true);
                    findPreference("EnableSets").setVisible(true);
                    findPreference("silenceHoursSwitch").setVisible(true);
                    findPreference("advancedDelivery").setVisible(false);

                    boolean silenceSwitch = sharedPreferences.getBoolean("silenceHoursSwitch", true);
                    if(silenceSwitch) {
                        findPreference("silenceHoursSettings").setVisible(true);
                    }
                    else {
                        findPreference("silenceHoursSettings").setVisible(false);
                    }

                    try {
                        JSONObject advancedFile = new JSONObject(JsonFile.readJson(context, "advancedDelivery.json"));

                        Iterator<String> iterator = advancedFile.keys();

                        while(iterator.hasNext()) {
                            String key = iterator.next();
                            NotificationHelper.cancelAdvancedAlarm(context, Integer.parseInt(key));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    notifiListPreference.setSummary(R.string.const_freq);
                }
                else if(value == 2) {
                    ConstNotifiSetup.CancelNotifications(context);

                    findPreference("timeAsk").setVisible(false);
                    findPreference("EnableSets").setVisible(false);
                    findPreference("silenceHoursSwitch").setVisible(false);
                    findPreference("silenceHoursSettings").setVisible(false);
                    findPreference("advancedDelivery").setVisible(true);

                    RegisterNotifications.registerAdvancedDelivery(context);

                    notifiListPreference.setSummary(R.string.advanced_notifi);
                }

                return true;
            }
        });

        Preference timeAsk = new Preference(context);
        timeAsk.setIconSpaceReserved(false);
        timeAsk.setKey("timeAsk");
        timeAsk.setTitle(R.string.FreqAskPref);
        timeAsk.setSummary(getString(R.string.FreqText) + " " + freq + " " + getString(R.string.minutes));
        timeAsk.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                RepeatsHelper.AskAboutTime(context, false, SettingsActivity.activity, null);
                return true;
            }
        });

        Preference enableSets = new Preference(context);
        enableSets.setIconSpaceReserved(false);
        enableSets.setKey("EnableSets");
        enableSets.setTitle(R.string.EnableTitle);
        enableSets.setSummary(R.string.EnableSummary);
        enableSets.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(context, EnableSetsListActivity.class);
                startActivity(intent);
                return true;
            }
        });

        Preference reset = new Preference(context);
        reset.setIconSpaceReserved(false);
        reset.setKey("reset");
        reset.setTitle(R.string.resetTitle);
        reset.setSummary(R.string.resetSummary);
        reset.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                DatabaseHelper DB = new DatabaseHelper(context);
                DB.ResetEnabled();

                RepeatsHelper.SaveFrequency(context, 5);
                ConstNotifiSetup.CancelNotifications(context);
                ConstNotifiSetup.RegisterNotifications(context, null, RepeatsHelper.staticFrequencyCode);

                int frequency = sharedPreferences.getInt("frequency", 0);
                findPreference("timeAsk").setSummary(getString(R.string.FreqText) + " " + frequency + " " + getString(R.string.minutes));

                Toast.makeText(context, getString(R.string.ResetSuccessfully), Toast.LENGTH_SHORT).show();
                return true;
            }
        });



        SwitchPreferenceCompat silenceHours = new SwitchPreferenceCompat(context);
        silenceHours.setIconSpaceReserved(false);
        silenceHours.setKey("silenceHoursSwitch");
        silenceHours.setTitle(R.string.silenceHours);
        silenceHours.setSummary(R.string.silenceHoursSummary);
        silenceHours.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                if((boolean)(newValue)){
                    findPreference("silenceHoursSettings").setVisible(true);
                }
                else {
                    findPreference("silenceHoursSettings").setVisible(false);
                }

                ConstNotifiSetup.CancelNotifications(context);
                ConstNotifiSetup.RegisterNotifications(context, null, RepeatsHelper.staticFrequencyCode);

                return true;
            }
        });

        Preference silenceHoursSettings = new Preference(context);
        silenceHoursSettings.setIconSpaceReserved(false);
        silenceHoursSettings.setKey("silenceHoursSettings");
        silenceHoursSettings.setTitle(R.string.changeSilenceHours);
        silenceHoursSettings.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {

                Intent intent = new Intent(context, SilenceHoursActivity.class);
                getActivity().startActivity(intent);

                return true;
            }
        });

        Preference advancedDelivery = new Preference(context);
        advancedDelivery.setIconSpaceReserved(false);
        advancedDelivery.setKey("advancedDelivery");
        advancedDelivery.setTitle(R.string.changeDelivery);
        advancedDelivery.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(context, ChangeDeliveryListActivity.class);
                getActivity().startActivity(intent);
                return true;
            }
        });

        Preference noSetsInDatabaseInfo = new Preference(context);
        Drawable drawable = context.getDrawable(R.drawable.ic_info_outline);
        if(RepeatsHelper.DarkTheme(context, true)){
            drawable.setColorFilter(Color.parseColor("#6d6d6d"), PorterDuff.Mode.SRC_IN);
        }
        else {
            drawable.setColorFilter(Color.parseColor("#bfbfbf"), PorterDuff.Mode.SRC_IN);
        }

        noSetsInDatabaseInfo.setIcon(drawable);
        noSetsInDatabaseInfo.setSummary(R.string.noSetsInfoSettings);
        noSetsInDatabaseInfo.setVisible(false);

        PreferenceCategory notification_category = new PreferenceCategory(context);
        notification_category.setIconSpaceReserved(false);
        notification_category.setKey("NotifiCat");
        notification_category.setTitle(R.string.notifications);
        screen.addPreference(notification_category);
        notification_category.addPreference(notifiListPreference);
        notification_category.addPreference(timeAsk);
        notification_category.addPreference(enableSets);
        notification_category.addPreference(silenceHours);
        notification_category.addPreference(silenceHoursSettings);
        notification_category.addPreference(advancedDelivery);
        notification_category.addPreference(noSetsInDatabaseInfo);

        final ListPreference theme = new ListPreference(context);
        theme.setIconSpaceReserved(false);
        theme.setKey("theme");
        theme.setTitle(R.string.changeTheme);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            theme.setEntries(R.array.themesQ);
            theme.setEntryValues(R.array.ThemeValuesQ);
            theme.setValue("2");
        } else {
            theme.setEntries(R.array.themes);
            theme.setEntryValues(R.array.ThemeValues);
            theme.setValue("1");
        }
        theme.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                RepeatsHelper.resetActivity(context, getActivity());

                return true;
            }
        });

        PreferenceCategory theme_cat = new PreferenceCategory(context);
        theme_cat.setIconSpaceReserved(false);
        theme_cat.setKey("theme_cat");
        theme_cat.setTitle(R.string.Theme);
        screen.addPreference(theme_cat);
        theme_cat.addPreference(theme);

        Preference createBackup = new Preference(context);
        createBackup.setIconSpaceReserved(false);
        createBackup.setKey("create_backup");
        createBackup.setTitle(R.string.createBackup);
        createBackup.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Backup.createBackup(context, getActivity());
                return true;
            }
        });

        Preference restoreBackup = new Preference(context);
        restoreBackup.setIconSpaceReserved(false);
        restoreBackup.setKey("restore_backup");
        restoreBackup.setTitle(R.string.restoreBackup);
        restoreBackup.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Backup.selectFileToRestore(context, getActivity());
                return true;
            }
        });

        PreferenceCategory backup = new PreferenceCategory(context);
        backup.setIconSpaceReserved(false);
        backup.setKey("backup");
        backup.setTitle(R.string.backup);
        screen.addPreference(backup);
        backup.addPreference(createBackup);
        backup.addPreference(restoreBackup);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            CheckBoxPreference optimizationPreference = new CheckBoxPreference(context);
            optimizationPreference.setIconSpaceReserved(false);
            optimizationPreference.setKey("batteryOptimization");
            optimizationPreference.setTitle(R.string.batteryO);
            optimizationPreference.setSummary(R.string.batteryOsummary);
            optimizationPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if ((Boolean) newValue) {
                        String packageName = context.getPackageName();
                        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                        if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                            Intent intent = new Intent();
                            intent.setAction(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setData(Uri.parse("package:" + packageName));

                            startActivityForResult(intent, RequestCodes.REQUEST_IGNORE_BATTERY);
                        }
                    } else {
                        String packageName = context.getPackageName();
                        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                        if (pm.isIgnoringBatteryOptimizations(packageName)) {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);

                            startActivityForResult(intent, RequestCodes.OPEN_BATTERY_SETTINGS);
                        }
                    }
                    return true;
                }
            });

            PreferenceCategory batteryOptimizationCat = new PreferenceCategory(context);
            batteryOptimizationCat.setIconSpaceReserved(false);
            batteryOptimizationCat.setKey("batteryCat");
            batteryOptimizationCat.setTitle(R.string.batteryOptimization);
            screen.addPreference(batteryOptimizationCat);
            batteryOptimizationCat.addPreference(optimizationPreference);
        }

        PackageInfo pInfo = null;
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        final String version = pInfo.versionName;


        Preference About = new Preference(context);
        About.setIconSpaceReserved(false);
        About.setKey("about");
        About.setTitle(null);
        About.setSummary("Repeats " + version + "\n" + "Developer: Jakub Sieradzki" + "\n\n" + getString(R.string.specialThanksDawid));
        About.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                cliked++;
                if(cliked == 5) {
                    Intent intent = new Intent(context, FirstRunActivity.class);
                    startActivity(intent);
                }
                return true;
            }
        });

        Preference SendFeedback = new Preference(context);
        SendFeedback.setIconSpaceReserved(false);
        SendFeedback.setKey("feedback");
        SendFeedback.setTitle(R.string.SendFeedback);
        SendFeedback.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent send = new Intent(Intent.ACTION_SEND);
                send.setType("plain/text");
                send.putExtra(Intent.EXTRA_EMAIL, new String[]{"rootekstudio@outlook.com"});
                send.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.FeedbackSubject) + " " + version);

                startActivity(Intent.createChooser(send, getString(R.string.SendFeedback)));

                return true;
            }
        });

        Preference rate = new Preference(context);
        rate.setIconSpaceReserved(false);
        rate.setKey("rate");
        rate.setTitle(R.string.rate);
        rate.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent send = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.rootekstudio.repeatsandroid"));
                try {
                    startActivity(send);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(context, R.string.storeNotFound, Toast.LENGTH_LONG).show();
                }

                return true;
            }
        });

        PreferenceCategory about_cat = new PreferenceCategory(context);
        about_cat.setIconSpaceReserved(false);
        about_cat.setKey("about_cat");
        about_cat.setTitle(R.string.About);
        screen.addPreference(about_cat);
        about_cat.addPreference(rate);
        about_cat.addPreference(SendFeedback);
        about_cat.addPreference(About);

        screen.setIconSpaceReserved(false);
        setPreferenceScreen(screen);

        //Load settings

        if (all.size() == 0) {
            createBackup.setVisible(false);
            noSetsInDatabaseInfo.setVisible(true);
            notification_category.setEnabled(false);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            findPreference("batteryOptimization").setVisible(true);
        }
        else {
            findPreference("batteryOptimization").setVisible(false);
        }

        int getNotifiMode = Integer.parseInt(sharedPreferences.getString("ListNotifi", "0"));

        if (getNotifiMode == 0) {
            findPreference("timeAsk").setVisible(false);
            findPreference("EnableSets").setVisible(false);
            findPreference("silenceHoursSwitch").setVisible(false);
            findPreference("silenceHoursSettings").setVisible(false);
            findPreference("advancedDelivery").setVisible(false);

            notifiListPreference.setSummary(R.string.turned_off);

        } else if(getNotifiMode == 1) {
            findPreference("timeAsk").setVisible(true);
            findPreference("EnableSets").setVisible(true);
            findPreference("silenceHoursSwitch").setVisible(true);
            findPreference("advancedDelivery").setVisible(false);
            boolean switchSilence = sharedPreferences.getBoolean("silenceHoursSwitch", true);
            if(switchSilence) {
                findPreference("silenceHoursSettings").setVisible(true);
            }
            else {
                findPreference("silenceHoursSettings").setVisible(false);
            }

            notifiListPreference.setSummary(R.string.const_freq);
        }

        else if(getNotifiMode == 2) {
            findPreference("timeAsk").setVisible(false);
            findPreference("EnableSets").setVisible(false);
            findPreference("silenceHoursSwitch").setVisible(false);
            findPreference("silenceHoursSettings").setVisible(false);
            findPreference("advancedDelivery").setVisible(true);

            notifiListPreference.setSummary(R.string.advanced_notifi);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String packageName = context.getPackageName();
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            SharedPreferences.Editor edit = sharedPreferences.edit();
            if (requestCode == 123) {
                if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                    edit.putBoolean("batteryOptimization", false);
                    edit.apply();
                } else {
                    edit.putBoolean("batteryOptimization", true);
                    edit.apply();
                }

                this.getActivity().finish();
                this.getActivity().overridePendingTransition(0, 0);
                startActivity(this.getActivity().getIntent());
                this.getActivity().overridePendingTransition(0, 0);
            } else if (requestCode == 124) {
                if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                    edit.putBoolean("batteryOptimization", false);
                    edit.apply();
                } else {
                    edit.putBoolean("batteryOptimization", true);
                    edit.apply();
                }
            }
        }
    }
}
