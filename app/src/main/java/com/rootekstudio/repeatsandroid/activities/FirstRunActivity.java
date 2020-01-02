package com.rootekstudio.repeatsandroid.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.RepeatsHelper;
import com.rootekstudio.repeatsandroid.database.DatabaseHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

public class FirstRunActivity extends AppCompatActivity {
    int selected = 0;
    Context context;
    ViewFlipper viewFlipper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean dark = RepeatsHelper.DarkTheme(this, false);
        setContentView(R.layout.activity_first_run);
        defaultSettings();
        selected = 1;
        context = this;
        getSupportActionBar().hide();

        if(!dark) {
            ImageView imageView = findViewById(R.id.logoFirstRun);
            imageView.setImageResource(R.drawable.repeats_for_light_bg);
        }

        Animation in = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        viewFlipper = findViewById(R.id.view_flipper_firstrun);
        viewFlipper.setInAnimation(in);

        TextView terms = findViewById(R.id.termsOfUseView);
        terms.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void nextClick(View view) {
        if(viewFlipper.getDisplayedChild() == 2) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            if (!sharedPreferences.contains("firstRunTerms")) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("firstRunTerms", selected);
                editor.apply();
            }

            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            viewFlipper.showNext();
        }
    }

    public void radioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        TextView txtView = findViewById(R.id.defaultInfo);

        if (view.getId() == R.id.constFreqButton) {
            if (checked) {
                txtView.setText(R.string.freqDefault);
                selected = 1;
            }
        } else {
            if (checked) {
                txtView.setText(R.string.advancedDefault);
                selected = 2;
            }
        }
    }

    void defaultSettings() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (!sharedPreferences.contains("silenceHoursSwitch")) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("silenceHoursSwitch", true);
            editor.apply();
        }

        if (!sharedPreferences.contains("frequency")) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("frequency", 30);
            editor.apply();
        }

        File jsonFile = new File(getFilesDir(), "silenceHours.json");
        if (!jsonFile.exists()) {
            try {
                JSONObject values = new JSONObject();
                values.put("from", "22:00");
                values.put("to", "06:00");

                JSONObject json = new JSONObject();
                json.put("0", values);

                FileWriter fileWriter = new FileWriter(jsonFile);
                String jsonSTRING = json.toString();
                fileWriter.append(jsonSTRING);
                fileWriter.flush();
                fileWriter.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        File jsonAdvanced = new File(getFilesDir(), "advancedDelivery.json");
        if (!jsonAdvanced.exists()) {
            try {
                JSONObject rootObject = new JSONObject();

                JSONObject values = new JSONObject();

                //days
                JSONArray daysArray = new JSONArray();
                for (int i = 2; i <= 7; i++) {
                    daysArray.put(String.valueOf(i));
                }
                daysArray.put(String.valueOf(1));

                values.put("days", daysArray);

                //hours
                JSONObject hours = new JSONObject();
                JSONObject singleHour = new JSONObject();

                singleHour.put("from", "08:00");
                singleHour.put("to", "22:00");

                hours.put("0", singleHour);

                values.put("hours", hours);

                //frequency
                values.put("frequency", "30");

                //sets
                JSONArray sets = new JSONArray();
                DatabaseHelper DB = new DatabaseHelper(this);
                ArrayList<String> nameList = DB.getSingleColumn("TableName");
                int setsCount = nameList.size();

                for (int i = 0; i < setsCount; i++) {
                    sets.put(nameList.get(i));
                }
                values.put("sets", sets);

                //put everything to one json
                rootObject.put("0", values);

                FileWriter fileWriter = new FileWriter(jsonAdvanced);
                String jsonSTRING = rootObject.toString();
                fileWriter.write(jsonSTRING);
                fileWriter.flush();
                fileWriter.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
