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
import androidx.preference.SwitchPreferenceCompat;

import com.rootekstudio.repeatsandroid.activities.ChangeDeliveryListActivity;
import com.rootekstudio.repeatsandroid.activities.EnableSetsListActivity;
import com.rootekstudio.repeatsandroid.activities.FirstRunActivity;
import com.rootekstudio.repeatsandroid.activities.MainActivity;
import com.rootekstudio.repeatsandroid.activities.SettingsActivity;
import com.rootekstudio.repeatsandroid.activities.SilenceHoursActivity;
import com.rootekstudio.repeatsandroid.activities.WhatsNewActivity;
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
        setPreferencesFromResource(R.xml.preference_screen, rootKey);

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
        DatabaseHelper DB = new DatabaseHelper(context);
        List<RepeatsSetInfo> all = DB.AllItemsLIST();

        final int freq = sharedPreferences.getInt("frequency", 0);

        final ListPreference notifiListPreference = findPreference("ListNotifi");

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

        Preference timeAsk = findPreference("timeAsk");
        timeAsk.setSummary(getString(R.string.FreqText) + " " + freq + " " + getString(R.string.minutes));
        timeAsk.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                RepeatsHelper.AskAboutTime(context, false, SettingsActivity.activity, null);
                return true;
            }
        });

        Preference enableSets = findPreference("EnableSets");
        enableSets.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(context, EnableSetsListActivity.class);
                startActivity(intent);
                return true;
            }
        });

        SwitchPreferenceCompat silenceHours = findPreference("silenceHoursSwitch");
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

        Preference silenceHoursSettings = findPreference("silenceHoursSettings");
        silenceHoursSettings.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {

                Intent intent = new Intent(context, SilenceHoursActivity.class);
                getActivity().startActivity(intent);

                return true;
            }
        });

        Preference advancedDelivery = findPreference("advancedDelivery");
        advancedDelivery.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(context, ChangeDeliveryListActivity.class);
                getActivity().startActivity(intent);
                return true;
            }
        });

        Preference noSetsInDatabaseInfo = findPreference("noSetsInDatabaseInfo");
        Drawable drawable = context.getDrawable(R.drawable.ic_info_outline);
        if(RepeatsHelper.DarkTheme(context, true)){
            drawable.setColorFilter(Color.parseColor("#6d6d6d"), PorterDuff.Mode.SRC_IN);
        }
        else {
            drawable.setColorFilter(Color.parseColor("#bfbfbf"), PorterDuff.Mode.SRC_IN);
        }
        noSetsInDatabaseInfo.setIcon(drawable);

        final ListPreference theme = findPreference("theme");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            theme.setEntries(R.array.themesQ);
            theme.setEntryValues(R.array.ThemeValuesQ);
            theme.setDefaultValue("2");
        } else {
            theme.setEntries(R.array.themes);
            theme.setEntryValues(R.array.ThemeValues);
            theme.setDefaultValue("1");
        }
        theme.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                else {
                    RepeatsHelper.resetActivity(context, getActivity());
                }
                return true;
            }
        });

        Preference createBackup = findPreference("create_backup");
        createBackup.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Backup.createBackup(context, getActivity());
                return true;
            }
        });

        Preference restoreBackup = findPreference("restore_backup");
        restoreBackup.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Backup.selectFileToRestore(context, getActivity());
                return true;
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            CheckBoxPreference optimizationPreference = findPreference("batteryOptimization");
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


        }
        else {
            PreferenceCategory batteryOptimizationCat = findPreference("batteryOptimizationCategory");
            batteryOptimizationCat.setVisible(false);
        }

        PackageInfo pInfo = null;
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        final String version = pInfo.versionName;


        Preference About = findPreference("about");
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

        Preference SendFeedback = findPreference("feedback");
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

        Preference rate = findPreference("rate");
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

        Preference terms = findPreference("terms");
        terms.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent send = new Intent(Intent.ACTION_VIEW, Uri.parse("https://rootekstudio.wordpress.com/warunki-uzytkowania"));
                try {
                    startActivity(send);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(context, R.string.browserNotFound, Toast.LENGTH_LONG).show();
                }

                return true;
            }
        });

        Preference privacy = findPreference("privacy");
        privacy.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent send = new Intent(Intent.ACTION_VIEW, Uri.parse("https://rootekstudio.wordpress.com/polityka-prywatnosci"));
                try {
                    startActivity(send);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(context, R.string.browserNotFound, Toast.LENGTH_LONG).show();
                }

                return true;
            }
        });
        Preference whatsNew = findPreference("whatsNew");
        whatsNew.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(context, WhatsNewActivity.class);
                startActivity(intent);
                return true;
            }
        });
        //Load settings

        if (all.size() == 0) {
            createBackup.setVisible(false);
            noSetsInDatabaseInfo.setVisible(true);
            findPreference("notificationsGroup").setEnabled(false);
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
                RepeatsHelper.resetActivity(context, getActivity());
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
