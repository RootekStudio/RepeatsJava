package com.rootekstudio.repeatsandroid.mainpage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.CheckBoxPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;

import com.rootekstudio.repeatsandroid.Backup;
import com.rootekstudio.repeatsandroid.JsonFile;
import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.RepeatsHelper;
import com.rootekstudio.repeatsandroid.RequestCodes;
import com.rootekstudio.repeatsandroid.activities.AppInfoActivity;
import com.rootekstudio.repeatsandroid.activities.ChangeDeliveryListActivity;
import com.rootekstudio.repeatsandroid.activities.EnableSetsListActivity;
import com.rootekstudio.repeatsandroid.activities.SilenceHoursActivity;
import com.rootekstudio.repeatsandroid.activities.WhatsNewActivity;
import com.rootekstudio.repeatsandroid.database.RepeatsDatabase;
import com.rootekstudio.repeatsandroid.database.Values;
import com.rootekstudio.repeatsandroid.notifications.ConstNotifiSetup;
import com.rootekstudio.repeatsandroid.notifications.NotificationHelper;
import com.rootekstudio.repeatsandroid.notifications.RegisterNotifications;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class PreferenceFragment extends PreferenceFragmentCompat {
    private SharedPreferences sharedPreferences;

    public PreferenceFragment() {

    }

    @Override
    public void onCreatePreferences(final Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preference_screen, rootKey);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        RepeatsHelper.askAboutBattery(getContext());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String packageName = getContext().getPackageName();
            PowerManager pm = (PowerManager) getContext().getSystemService(Context.POWER_SERVICE);
            SharedPreferences.Editor edit = sharedPreferences.edit();
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                edit.putBoolean("batteryOptimization", false);
                edit.apply();
            } else {
                edit.putBoolean("batteryOptimization", true);
                edit.apply();
            }
        }

        final int freq = sharedPreferences.getInt("frequency", 0);

        final ListPreference notifiListPreference = findPreference("ListNotifi");

        notifiListPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                int value = Integer.parseInt((String) newValue);

                if (value == 0) {
                    ConstNotifiSetup.CancelNotifications(getContext());

                    findPreference("timeAsk").setVisible(false);
                    findPreference("EnableSets").setVisible(false);
                    findPreference("silenceHoursSwitch").setVisible(false);
                    findPreference("silenceHoursSettings").setVisible(false);
                    findPreference("advancedDelivery").setVisible(false);

                    notifiListPreference.setSummary(R.string.turned_off);

                    try {
                        JSONObject advancedFile = new JSONObject(JsonFile.readJson(getContext(), "advancedDelivery.json"));

                        Iterator<String> iterator = advancedFile.keys();

                        while (iterator.hasNext()) {
                            String key = iterator.next();
                            NotificationHelper.cancelAdvancedAlarm(getContext(), Integer.parseInt(key));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else if (value == 1) {

                    ConstNotifiSetup.RegisterNotifications(getContext(), null, RepeatsHelper.staticFrequencyCode);

                    findPreference("timeAsk").setVisible(true);
                    findPreference("EnableSets").setVisible(true);
                    findPreference("silenceHoursSwitch").setVisible(true);
                    findPreference("advancedDelivery").setVisible(false);

                    boolean silenceSwitch = sharedPreferences.getBoolean("silenceHoursSwitch", true);
                    if (silenceSwitch) {
                        findPreference("silenceHoursSettings").setVisible(true);
                    } else {
                        findPreference("silenceHoursSettings").setVisible(false);
                    }

                    try {
                        JSONObject advancedFile = new JSONObject(JsonFile.readJson(getContext(), "advancedDelivery.json"));

                        Iterator<String> iterator = advancedFile.keys();

                        while (iterator.hasNext()) {
                            String key = iterator.next();
                            NotificationHelper.cancelAdvancedAlarm(getContext(), Integer.parseInt(key));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    notifiListPreference.setSummary(R.string.const_freq);
                } else if (value == 2) {
                    ConstNotifiSetup.CancelNotifications(getContext());

                    findPreference("timeAsk").setVisible(false);
                    findPreference("EnableSets").setVisible(false);
                    findPreference("silenceHoursSwitch").setVisible(false);
                    findPreference("silenceHoursSettings").setVisible(false);
                    findPreference("advancedDelivery").setVisible(true);

                    RegisterNotifications.registerAdvancedDelivery(getContext());

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
                RepeatsHelper.AskAboutTime(getContext(), (AppCompatActivity) getActivity());
                return true;
            }
        });

        Preference enableSets = findPreference("EnableSets");
        enableSets.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivity(new Intent(getContext(), EnableSetsListActivity.class));
                return true;
            }
        });

        SwitchPreferenceCompat silenceHours = findPreference("silenceHoursSwitch");
        silenceHours.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                if ((boolean) (newValue)) {
                    findPreference("silenceHoursSettings").setVisible(true);
                } else {
                    findPreference("silenceHoursSettings").setVisible(false);
                }

                ConstNotifiSetup.CancelNotifications(getContext());
                ConstNotifiSetup.RegisterNotifications(getContext(), null, RepeatsHelper.staticFrequencyCode);

                return true;
            }
        });

        Preference silenceHoursSettings = findPreference("silenceHoursSettings");
        silenceHoursSettings.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivity(new Intent(getContext(), SilenceHoursActivity.class));
                return true;
            }
        });

        Preference advancedDelivery = findPreference("advancedDelivery");
        advancedDelivery.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivity( new Intent(getContext(), ChangeDeliveryListActivity.class));
                return true;
            }
        });

        Preference noSetsInDatabaseInfo = findPreference("noSetsInDatabaseInfo");
        Drawable drawable = getContext().getDrawable(R.drawable.ic_info_outline);
        if (RepeatsHelper.DarkTheme(getContext(), true)) {
            drawable.setColorFilter(Color.parseColor("#6d6d6d"), PorterDuff.Mode.SRC_IN);
        } else {
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
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    RepeatsHelper.resetActivity(getContext(), getActivity());
                }
                return true;
            }
        });

        Preference createBackup = findPreference("create_backup");
        createBackup.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Backup.createBackup(getContext(), getActivity());
                return true;
            }
        });

        Preference restoreBackup = findPreference("restore_backup");
        restoreBackup.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Backup.selectFileToRestore(getContext(), getActivity());
                return true;
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            CheckBoxPreference optimizationPreference = findPreference("batteryOptimization");
            optimizationPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if ((Boolean) newValue) {
                        String packageName = getContext().getPackageName();
                        PowerManager pm = (PowerManager) getContext().getSystemService(Context.POWER_SERVICE);
                        if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                            Intent intent = new Intent();
                            intent.setAction(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setData(Uri.parse("package:" + packageName));

                            startActivityForResult(intent, RequestCodes.REQUEST_IGNORE_BATTERY);
                        }
                    } else {
                        String packageName = getContext().getPackageName();
                        PowerManager pm = (PowerManager) getContext().getSystemService(Context.POWER_SERVICE);
                        if (pm.isIgnoringBatteryOptimizations(packageName)) {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);

                            startActivityForResult(intent, RequestCodes.OPEN_BATTERY_SETTINGS);
                        }
                    }
                    return true;
                }
            });


        } else {
            PreferenceCategory batteryOptimizationCat = findPreference("batteryOptimizationCategory");
            batteryOptimizationCat.setVisible(false);
        }

        Preference about = findPreference("about");
        about.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivity(new Intent(getContext(), AppInfoActivity.class));
                return true;
            }
        });

        Preference whatsNew = findPreference("whatsNew");
        whatsNew.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivity(new Intent(getContext(), WhatsNewActivity.class));
                return true;
            }
        });


        //Load settings

        if (new RepeatsDatabase(getContext()).itemsInSetCount(Values.sets_info) == 0) {
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

        } else if (getNotifiMode == 1) {
            findPreference("timeAsk").setVisible(true);
            findPreference("EnableSets").setVisible(true);
            findPreference("silenceHoursSwitch").setVisible(true);
            findPreference("advancedDelivery").setVisible(false);
            boolean switchSilence = sharedPreferences.getBoolean("silenceHoursSwitch", true);
            if (switchSilence) {
                findPreference("silenceHoursSettings").setVisible(true);
            } else {
                findPreference("silenceHoursSettings").setVisible(false);
            }

            notifiListPreference.setSummary(R.string.const_freq);
        } else if (getNotifiMode == 2) {
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
            String packageName = getContext().getPackageName();
            PowerManager pm = (PowerManager) getContext().getSystemService(Context.POWER_SERVICE);
            SharedPreferences.Editor edit = sharedPreferences.edit();
            if (requestCode == 123) {
                if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                    edit.putBoolean("batteryOptimization", false);
                    edit.apply();
                } else {
                    edit.putBoolean("batteryOptimization", true);
                    edit.apply();
                }
                RepeatsHelper.resetActivity(getContext(), getActivity());
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
