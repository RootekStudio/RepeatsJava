package com.rootekstudio.repeatsandroid.mainpage;

import android.app.Dialog;
import android.app.TimePickerDialog;
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
import android.widget.TimePicker;
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
import com.rootekstudio.repeatsandroid.OnSystemBoot;
import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.RequestCodes;
import com.rootekstudio.repeatsandroid.UIHelper;
import com.rootekstudio.repeatsandroid.activities.AppInfoActivity;
import com.rootekstudio.repeatsandroid.activities.WhatsNewActivity;
import com.rootekstudio.repeatsandroid.database.RepeatsDatabase;
import com.rootekstudio.repeatsandroid.database.Values;
import com.rootekstudio.repeatsandroid.notifications.NotificationsScheduler;
import com.rootekstudio.repeatsandroid.reminders.SetReminders;
import com.rootekstudio.repeatsandroid.settings.ManageNotificationsActivity;
import com.rootekstudio.repeatsandroid.settings.ManageRemindersActivity;
import com.rootekstudio.repeatsandroid.settings.SharedPreferencesManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PreferenceFragment extends PreferenceFragmentCompat {
    private SharedPreferencesManager sharedPreferencesManager;
    private SharedPreferences sharedPreferences;
    boolean is24HourFormat;

    Dialog askTimeDialog;

    public PreferenceFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreatePreferences(final Bundle savedInstanceState, String rootKey) {
        is24HourFormat = android.text.format.DateFormat.is24HourFormat(getContext());

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

        SwitchPreferenceCompat notificationsEnabled = findPreference("notificationsEnabled");
        notificationsEnabled.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                sharedPreferencesManager.setNotificationsEnabled((boolean) newValue);
                if ((boolean) newValue) {
                    NotificationsScheduler.scheduleNotifications(getContext());
                    findPreference("timeAsk").setVisible(true);
                    findPreference("manageNotifications").setVisible(true);
                } else {
                    NotificationsScheduler.stopNotifications(getContext());
                    findPreference("timeAsk").setVisible(false);
                    findPreference("manageNotifications").setVisible(false);
                }
                return true;
            }
        });

        Preference timeAsk = findPreference("timeAsk");
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
                        if(Integer.parseInt(timeText) != 0) {
                            saveTime(timeText);
                            askTimeDialog.dismiss();
                        } else {
                            Toast.makeText(getContext(), getString(R.string.frequency_cannot_be_0), Toast.LENGTH_SHORT).show();
                        }
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
                    if(Integer.parseInt(text) != 0) {
                        saveTime(text);
                    } else {
                        Toast.makeText(getContext(), getString(R.string.frequency_cannot_be_0), Toast.LENGTH_SHORT).show();

                    }
                }
            });
            askTimeDialog = dialogBuilder.show();

            return true;
        });

        Preference manageNotifications = findPreference("manageNotifications");
        manageNotifications.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivity(new Intent(getContext(), ManageNotificationsActivity.class));
                return true;
            }
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

        SwitchPreferenceCompat remindersEnabled = findPreference(SharedPreferencesManager.REMINDERS_ENABLED_KEY);
        remindersEnabled.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                sharedPreferencesManager.setRemindersEnabled((boolean) newValue);
                if ((boolean) newValue) {
                    SetReminders.startReminders(getContext());

                    findPreference(SharedPreferencesManager.REMINDERS_TIME_KEY).setVisible(true);
                    findPreference("manageReminders").setVisible(true);
                } else {
                    SetReminders.stopReminders(getContext());

                    findPreference(SharedPreferencesManager.REMINDERS_TIME_KEY).setVisible(false);
                    findPreference("manageReminders").setVisible(false);
                }

                return true;
            }
        });

        Preference remindersTime = findPreference(SharedPreferencesManager.REMINDERS_TIME_KEY);
        remindersTime.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hour, int minute) {
                        String stringHour;
                        String stringMinute;
                        if (hour <= 9) {
                            stringHour = "0" + hour;
                        } else {
                            stringHour = String.valueOf(hour);
                        }

                        if (minute <= 9) {
                            stringMinute = "0" + minute;
                        } else {
                            stringMinute = String.valueOf(minute);
                        }

                        String time = stringHour + ":" + stringMinute;

                        if (is24HourFormat) {
                            remindersTime.setSummary(getResources().getString(R.string.reminders_come_at) + " " + time);
                        } else {
                            SimpleDateFormat displayFormat = new SimpleDateFormat("hh:mm a");
                            SimpleDateFormat parseFormat = new SimpleDateFormat("HH:mm");
                            Date date = null;
                            try {
                                date = parseFormat.parse(time);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            remindersTime.setSummary(getResources().getString(R.string.reminders_come_at) + " " + displayFormat.format(date));
                        }

                        sharedPreferencesManager.setRemindersTime(time);

                        SetReminders.restartReminders(getContext());

                        if (!SetReminders.checkIfReminderIsRegistered(getContext())) {
                            remindersEnabled.setChecked(false);
                        }
                    }
                };

                int oldHour = Integer.parseInt(sharedPreferencesManager.getRemindersTime().substring(0, 2));
                int oldMinute = Integer.parseInt(sharedPreferencesManager.getRemindersTime().substring(3, 5));

                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), timeSetListener, oldHour,
                        oldMinute, is24HourFormat);
                timePickerDialog.show();

                return true;
            }
        });

        Preference manageReminders = findPreference("manageReminders");
        manageReminders.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivity(new Intent(getContext(), ManageRemindersActivity.class));
                return true;
            }
        });

        Preference createBackup = findPreference("create_backup");
        createBackup.setOnPreferenceClickListener(preference -> {
            Backup.createBackup(getContext(), getActivity());
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
        notificationsEnabled.setChecked(sharedPreferencesManager.getNotificationsEnabled());
        timeAsk.setVisible(sharedPreferencesManager.getNotificationsEnabled());
        manageNotifications.setVisible(sharedPreferencesManager.getNotificationsEnabled());

        int freq = sharedPreferencesManager.getFrequency();
        timeAsk.setSummary(getResources().getQuantityString(R.plurals.frequencyText, freq, freq));

        if (sharedPreferencesManager.getRemindersEnabled()) {
            remindersEnabled.setChecked(true);

            if (android.text.format.DateFormat.is24HourFormat(getContext())) {
                remindersTime.setSummary(getResources().getString(R.string.reminders_come_at) + " " + sharedPreferencesManager.getRemindersTime());
            } else {
                SimpleDateFormat displayFormat = new SimpleDateFormat("hh:mm a");
                SimpleDateFormat parseFormat = new SimpleDateFormat("HH:mm");
                Date date = null;
                try {
                    date = parseFormat.parse(sharedPreferencesManager.getRemindersTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                remindersTime.setSummary(getResources().getString(R.string.reminders_come_at) + " " + displayFormat.format(date));
            }
        } else {
            remindersEnabled.setChecked(false);
            remindersTime.setVisible(false);
            manageReminders.setVisible(false);
        }

        if (RepeatsDatabase.getInstance(getContext()).itemsInSetCount(Values.sets_info) == 0) {
            createBackup.setVisible(false);
            noSetsInDatabaseInfo.setVisible(true);
            findPreference("notificationsGroup").setEnabled(false);
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
        NotificationsScheduler.restartNotifications(getContext());

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
