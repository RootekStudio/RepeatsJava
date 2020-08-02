package com.rootekstudio.repeatsandroid.mainpage;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.InputFilter;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.CheckBoxPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.transition.MaterialFadeThrough;
import com.rootekstudio.repeatsandroid.Backup;
import com.rootekstudio.repeatsandroid.backup.CreateBackupActivity;
import com.rootekstudio.repeatsandroid.JsonFile;
import com.rootekstudio.repeatsandroid.OnSystemBoot;
import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.RepeatsHelper;
import com.rootekstudio.repeatsandroid.RequestCodes;
import com.rootekstudio.repeatsandroid.settings.SharedPreferencesManager;
import com.rootekstudio.repeatsandroid.UIHelper;
import com.rootekstudio.repeatsandroid.activities.AppInfoActivity;
import com.rootekstudio.repeatsandroid.settings.AdvancedDeliveryListActivity;
import com.rootekstudio.repeatsandroid.settings.EnableSetsListActivity;
import com.rootekstudio.repeatsandroid.settings.SilenceHoursActivity;
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
    private SharedPreferencesManager sharedPreferencesManager;
    private SharedPreferences sharedPreferences;

    public PreferenceFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setEnterTransition(new MaterialFadeThrough());
        setExitTransition(new MaterialFadeThrough());
    }

    @Override
    public void onCreatePreferences(final Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preference_screen, rootKey);
        sharedPreferencesManager = SharedPreferencesManager.getInstance(requireContext());
        sharedPreferences = sharedPreferencesManager.getSharedPreferences();
        askAboutBattery();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String packageName = requireContext().getPackageName();
            PowerManager pm = (PowerManager) requireContext().getSystemService(Context.POWER_SERVICE);
            SharedPreferences.Editor edit = sharedPreferences.edit();
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                edit.putBoolean("batteryOptimization", false);
                edit.apply();
            } else {
                edit.putBoolean("batteryOptimization", true);
                edit.apply();
            }
        }

        int freq = sharedPreferences.getInt("frequency", 0);

        ListPreference notifiListPreference = findPreference("ListNotifi");
        notifiListPreference.setOnPreferenceChangeListener((preference, newValue) -> {

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
        });

        Preference timeAsk = findPreference("timeAsk");
        timeAsk.setSummary(getString(R.string.FreqText) + " " + freq + " " + getString(R.string.minutes));
        timeAsk.setOnPreferenceClickListener(preference -> {
            MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(requireContext());
            dialogBuilder.setBackground(requireContext().getDrawable(R.drawable.dialog_shape));
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());

            View view1 = layoutInflater.inflate(R.layout.ask, null);
            final EditText editText = view1.findViewById(R.id.EditAsk);
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            editText.requestFocus();
            editText.setHint(R.string.enterNumber);
            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});

            editText.setOnKeyListener((view, keyCode, keyEvent) -> {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    EditText time = (EditText) view;
                    String timeText = time.getText().toString();
                    if (!timeText.equals("")) {
                        saveTime(timeText);
                    }
                }
                return false;
            });

            dialogBuilder.setView(view1);
            dialogBuilder.setTitle(R.string.QuestionFreq);
            dialogBuilder.setNegativeButton(R.string.Cancel, null);

            dialogBuilder.setPositiveButton(R.string.Save, (dialog, which) -> {
                String text = editText.getText().toString();
                if (!text.equals("")) {
                    saveTime(text);
                    ComponentName receiver = new ComponentName(requireContext(), OnSystemBoot.class);
                    PackageManager pm = requireContext().getPackageManager();

                    pm.setComponentEnabledSetting(receiver,
                            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                            PackageManager.DONT_KILL_APP);
                }
            });
            dialogBuilder.show();

            return true;
        });

        Preference enableSets = findPreference("EnableSets");
        enableSets.setOnPreferenceClickListener(preference -> {
            startActivity(new Intent(requireContext(), EnableSetsListActivity.class));
            return true;
        });

        SwitchPreferenceCompat silenceHours = findPreference("silenceHoursSwitch");
        silenceHours.setOnPreferenceChangeListener((preference, newValue) -> {

            if ((boolean) (newValue)) {
                findPreference("silenceHoursSettings").setVisible(true);
            } else {
                findPreference("silenceHoursSettings").setVisible(false);
            }

            ConstNotifiSetup.CancelNotifications(requireContext());
            ConstNotifiSetup.RegisterNotifications(requireContext(), null, RepeatsHelper.staticFrequencyCode);

            return true;
        });

        Preference silenceHoursSettings = findPreference("silenceHoursSettings");
        silenceHoursSettings.setOnPreferenceClickListener(preference -> {
            startActivity(new Intent(requireContext(), SilenceHoursActivity.class));
            return true;
        });

        Preference advancedDelivery = findPreference("advancedDelivery");
        advancedDelivery.setOnPreferenceClickListener(preference -> {
            startActivity(new Intent(requireContext(), AdvancedDeliveryListActivity.class));
            return true;
        });

        Preference noSetsInDatabaseInfo = findPreference("noSetsInDatabaseInfo");
        Drawable drawable = requireContext().getDrawable(R.drawable.info_circle);
        if (UIHelper.DarkTheme(getContext(), true)) {
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
        theme.setOnPreferenceChangeListener((preference, newValue) -> {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
                Intent intent = new Intent(requireContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else {
                UIHelper.restartActivity(requireActivity());
            }
            return true;
        });

        Preference createBackup = findPreference("create_backup");
        createBackup.setOnPreferenceClickListener(preference -> {
            startActivity(new Intent(requireContext(), CreateBackupActivity.class));
            return true;
        });

        Preference restoreBackup = findPreference("restore_backup");
        restoreBackup.setOnPreferenceClickListener(preference -> {
            Backup.selectFileToRestore(getContext(), requireActivity());
            return true;
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            CheckBoxPreference optimizationPreference = findPreference("batteryOptimization");
            optimizationPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                if ((Boolean) newValue) {
                    String packageName = requireContext().getPackageName();
                    PowerManager pm = (PowerManager) requireContext().getSystemService(Context.POWER_SERVICE);
                    if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setData(Uri.parse("package:" + packageName));

                        startActivityForResult(intent, RequestCodes.REQUEST_IGNORE_BATTERY);
                    }
                } else {
                    String packageName = requireContext().getPackageName();
                    PowerManager pm = (PowerManager) requireContext().getSystemService(Context.POWER_SERVICE);
                    if (pm.isIgnoringBatteryOptimizations(packageName)) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);

                        startActivityForResult(intent, RequestCodes.OPEN_BATTERY_SETTINGS);
                    }
                }
                return true;
            });


        } else {
            PreferenceCategory batteryOptimizationCat = findPreference("batteryOptimizationCategory");
            batteryOptimizationCat.setVisible(false);
        }

        Preference about = findPreference("about");
        about.setOnPreferenceClickListener(preference -> {
            startActivity(new Intent(requireContext(), AppInfoActivity.class));
            return true;
        });

        Preference whatsNew = findPreference("whatsNew");
        whatsNew.setOnPreferenceClickListener(preference -> {
            startActivity(new Intent(requireContext(), WhatsNewActivity.class));
            return true;
        });


        //Load settings

        if (RepeatsDatabase.getInstance(getContext()).itemsInSetCount(Values.sets_info) == 0) {
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

    private void askAboutBattery() {
        if (!sharedPreferences.contains("batteryOptimization")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                final String packageName = requireContext().getPackageName();
                PowerManager pm = (PowerManager) requireContext().getSystemService(Context.POWER_SERVICE);

                if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                    MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(requireContext());
                    dialog.setBackground(requireContext().getDrawable(R.drawable.dialog_shape));
                    dialog.setTitle(R.string.batteryAskTitle);
                    dialog.setMessage(R.string.batteryAskMessage);
                    dialog.setNegativeButton(R.string.Cancel, (dialog12, which) -> {
                        Toast.makeText(requireContext(), R.string.CancelOffBattery, Toast.LENGTH_LONG).show();
                        sharedPreferencesManager.setBatteryOptimization(false);

                    });

                    dialog.setPositiveButton(R.string.Continue, (dialog1, which) -> {

                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setData(Uri.parse("package:" + packageName));
                        requireActivity().startActivity(intent);

                        sharedPreferencesManager.setBatteryOptimization(true);

                    });

                    dialog.show();
                }
            }
        }
    }

    private void saveTime(String text) {
        int frequency = Integer.parseInt(text);

        sharedPreferencesManager.setFrequency(frequency);
        ConstNotifiSetup.CancelNotifications(requireContext());
        ConstNotifiSetup.RegisterNotifications(requireContext(), null, RepeatsHelper.staticFrequencyCode);

        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayoutMain, new PreferenceFragment());
        fragmentTransaction.commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String packageName = getContext().getPackageName();
            PowerManager pm = (PowerManager) getContext().getSystemService(Context.POWER_SERVICE);
            if (requestCode == 123) {
                if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                    sharedPreferencesManager.setBatteryOptimization(false);
                } else {
                    sharedPreferencesManager.setBatteryOptimization(true);
                }

                UIHelper.restartActivity(requireActivity());

            } else if (requestCode == 124) {
                if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                    sharedPreferencesManager.setBatteryOptimization(false);
                } else {
                    sharedPreferencesManager.setBatteryOptimization(true);
                }
            }
        }
    }
}
