package com.rootekstudio.repeatsandroid.firstrun;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.rootekstudio.repeatsandroid.JsonFile;
import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.RepeatsHelper;
import com.rootekstudio.repeatsandroid.RepeatsSetInfo;
import com.rootekstudio.repeatsandroid.database.DatabaseHelper;
import com.rootekstudio.repeatsandroid.mainpage.MainActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class FirstRunActivity extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager2 viewPager;
    TextView skipFirstRun;
    TextView ready;
    ImageButton previous;
    ImageButton next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_run);
        defaultSettings();
        createSets();
        RepeatsHelper.createNotificationChannel(this);

        tabLayout = findViewById(R.id.tabsLayoutFirstRun);
        viewPager = findViewById(R.id.viewPagerFirstRun);
        skipFirstRun = findViewById(R.id.skipFirstRun);
        previous = findViewById(R.id.buttonBack);
        next = findViewById(R.id.buttonNext);
        ready = findViewById(R.id.readyTextView);

        FirstRunFragmentPagerAdapter pagerAdapter = new FirstRunFragmentPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == 0) {
                    previous.setVisibility(View.INVISIBLE);
                    skipFirstRun.setVisibility(View.VISIBLE);
                    ready.setVisibility(View.INVISIBLE);
                } else if (position == 1) {
                    previous.setVisibility(View.VISIBLE);
                    skipFirstRun.setVisibility(View.INVISIBLE);
                } else if (position == 2) {
                    next.setVisibility(View.VISIBLE);
                    ready.setVisibility(View.INVISIBLE);
                } else if (position == 3) {
                    next.setVisibility(View.INVISIBLE);
                    ready.setVisibility(View.VISIBLE);
                }
            }
        });

        new TabLayoutMediator(tabLayout, viewPager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        tab.view.setEnabled(false);
                    }
                }).attach();
    }

    public void mainActivityClick(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void backClick(View view) {
        if (viewPager.getCurrentItem() != 0) {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }

    public void nextClick(View view) {
        if (viewPager.getCurrentItem() != 3) {
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
        }
    }

    void createSets() {
        SimpleDateFormat s = new SimpleDateFormat("yyyyMMddHHmmss");
        String date = s.format(new Date());
        String id = "R" + date + "0";

        SimpleDateFormat createD = new SimpleDateFormat("dd.MM.yyyy");
        String createDate = createD.format(new Date());

        ArrayList<String> questions = new ArrayList<>();
        ArrayList<String> answers = new ArrayList<>();

        RepeatsSetInfo list;
        if (Locale.getDefault().toString().equals("pl_PL")) {
            list = new RepeatsSetInfo("Angielski kolory", id, createDate, "true", "", "false", "pl_PL", "en_GB");

            questions.add("Czerwony");
            questions.add("Zielony");
            questions.add("Niebieski");
            questions.add("Żółty");
            questions.add("Czarny");
            questions.add("Biały");
            questions.add("Różowy");
            questions.add("Pomarańczowy");
            questions.add("Fioletowy");
            questions.add("Brązowy");

            answers.add("Red");
            answers.add("Green");
            answers.add("Blue");
            answers.add("Yellow");
            answers.add("Black");
            answers.add("White");
            answers.add("Pink");
            answers.add("Orange");
            answers.add("Violet");
            answers.add("Brown");
        } else {
            list = new RepeatsSetInfo("Spanish colors", id, createDate, "true", "", "false", "en_US", "es_ES");

            questions.add("Red");
            questions.add("Green");
            questions.add("Blue");
            questions.add("Yellow");
            questions.add("Black");
            questions.add("White");
            questions.add("Pink");
            questions.add("Orange");
            questions.add("Violet");
            questions.add("Brown");

            answers.add("Rojo");
            answers.add("Verde");
            answers.add("Azul");
            answers.add("Amarillo");
            answers.add("Negro");
            answers.add("Blanco");
            answers.add("Rosa");
            answers.add("Naranja");
            answers.add("Violeta");
            answers.add("Marrón");
        }

        DatabaseHelper DB = new DatabaseHelper(this);
        DB.CreateSet(id);
        DB.AddName(list);
        DB.insertSetToDatabase(id, questions, answers, null);
        JsonFile.putSetToJSON(this, id);
    }

    void defaultSettings() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (!sharedPreferences.contains("silenceHoursSwitch")) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("silenceHoursSwitch", false);
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
