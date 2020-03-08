package com.rootekstudio.repeatsandroid.activities;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.RepeatsListDB;
import com.rootekstudio.repeatsandroid.database.DatabaseHelper;
import com.rootekstudio.repeatsandroid.readAloud.ReadAloudActivity;
import com.rootekstudio.repeatsandroid.readAloud.ReadAloudConnector;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class SetSettingsActivity extends AppCompatActivity {
    TextToSpeech textToSpeech;
    AutoCompleteTextView autoCompleteTextView;
    AutoCompleteTextView autoCompleteTextView1;
    DatabaseHelper DB;
    String setID;
    boolean fromReadAloud;
    RepeatsListDB singleSetInfo;
    CheckBox ignoreCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        setID = intent.getStringExtra("setID");
        fromReadAloud = intent.getBooleanExtra("fromReadAloud", false);
        DB = new DatabaseHelper(this);
        singleSetInfo = DB.getSingleItemLIST(setID);
        ignoreCheckBox = findViewById(R.id.ignoreCheckBox);
        if (singleSetInfo.getIgnoreChars().equals("true")) {
            ignoreCheckBox.setChecked(true);
        } else {
            ignoreCheckBox.setChecked(false);
        }
        ignoreCheckBox.setOnCheckedChangeListener(onCheckedChangeListener);

        initTTS();
    }

    CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
            if (checked) {
                DB.ignoreChars(setID, "true");
            } else {
                DB.ignoreChars(setID, "false");
            }
        }
    };

    private void initTTS() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                textToSpeech = new TextToSpeech(SetSettingsActivity.this, new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int i) {
                        if (i == TextToSpeech.SUCCESS) {
                            selectLanguages();
                        } else {
                            findViewById(R.id.linearSetLanguages).setVisibility(View.GONE);
                            Toast.makeText(SetSettingsActivity.this, getString(R.string.cannotLoadLangList), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
    }

    private void selectLanguages() {
        final HashMap<String, String> detailLocaleList = new HashMap<>();
        Locale[] locales = Locale.getAvailableLocales();
        List<String> displayedlocaleList = new ArrayList<>();

        for (Locale locale : locales) {
            int res = textToSpeech.isLanguageAvailable(locale);
            if (res == TextToSpeech.LANG_COUNTRY_AVAILABLE) {
                displayedlocaleList.add(locale.getDisplayName());
                detailLocaleList.put(locale.getDisplayName(), locale.toString());
            }
        }

        Collections.sort(displayedlocaleList);

        String localeString = DB.getValue("firstLanguage", "TitleTable", "TableName='" + setID + "'");
        String localeString1 = DB.getValue("secondLanguage", "TitleTable", "TableName='" + setID + "'");

        final Locale locale = new Locale(localeString.substring(0, localeString.indexOf("_")), localeString.substring(localeString.indexOf("_") + 1));
        final Locale locale1 = new Locale(localeString1.substring(0, localeString1.indexOf("_")), localeString1.substring(localeString1.indexOf("_") + 1));

        final String defaults = getString(R.string.question_lang) + ": " + locale.getDisplayName() + "\n"
                + getString(R.string.answer_lang) + ": " + locale1.getDisplayName();

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.select_language_single_item, displayedlocaleList);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final TextView defaultsLanguages = findViewById(R.id.defaultsLanguageSettings);
                defaultsLanguages.setText(defaults);

                autoCompleteTextView = findViewById(R.id.firstLanguageSelect);
                autoCompleteTextView1 = findViewById(R.id.secondLanguageSelect);
                autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        TextView autoCompleteTextView = (TextView) view;
                        String localeString = detailLocaleList.get(autoCompleteTextView.getText().toString());
                        if (localeString != null) {
                            DB.UpdateTable("TitleTable", "firstLanguage='" + localeString + "'", "TableName='" + setID + "'");
                            String defaults = getString(R.string.question_lang) + ": " + autoCompleteTextView.getText().toString() + "\n"
                                    + getString(R.string.answer_lang) + ": " + locale1.getDisplayName();

                            defaultsLanguages.setText(defaults);
                            ReadAloudConnector.locale0 = localeString;

                        }
                    }
                });

                autoCompleteTextView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        TextView autoCompleteTextView = (TextView) view;
                        String localeString = detailLocaleList.get(autoCompleteTextView.getText().toString());
                        if (localeString != null) {
                            DB.UpdateTable("TitleTable", "secondLanguage='" + localeString + "'", "TableName='" + setID + "'");
                            String defaults = getString(R.string.question_lang) + ": " + locale.getDisplayName() + "\n"
                                    + getString(R.string.answer_lang) + ": " + autoCompleteTextView.getText().toString();

                            defaultsLanguages.setText(defaults);
                            ReadAloudConnector.locale1 = localeString;
                        }
                    }
                });
                autoCompleteTextView.setAdapter(adapter);
                autoCompleteTextView1.setAdapter(adapter);
            }
        });
    }

    public void ignoreDescriptionClick(View view) {
        if (ignoreCheckBox.isChecked()) {
            ignoreCheckBox.setChecked(false);
        } else {
            ignoreCheckBox.setChecked(true);
        }

    }

    public void swapQuestionsWithAnswersClick(View view) {
        view.setEnabled(false);
        LinearLayout linearLayout = findViewById(R.id.operationInfo);
        TextView textView = linearLayout.findViewById(R.id.operationInfoTextView);
        ImageView imageView = linearLayout.findViewById(R.id.operationInfoImageView);

        try {
            DB.swapQuestionsWithAnswers(setID);

            imageView.setImageDrawable(getDrawable(R.drawable.ic_check));
            imageView.setColorFilter(ContextCompat.getColor(this, R.color.greenRepeats), android.graphics.PorterDuff.Mode.SRC_IN);
            textView.setText(R.string.operationSuccess);
        } catch (Exception e) {
            imageView.setImageDrawable(getDrawable(R.drawable.ic_clear));
            imageView.setColorFilter(ContextCompat.getColor(this, R.color.redRepeats), android.graphics.PorterDuff.Mode.SRC_IN);
            textView.setText(R.string.operationFailed);

            e.printStackTrace();
        }

        linearLayout.setVisibility(View.VISIBLE);
        view.setEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (fromReadAloud && !ReadAloudConnector.isTTSStopped) {
            Intent intent = new Intent(this, ReadAloudActivity.class);
            intent.putExtra("setID", setID);
            intent.putExtra("loadedFromNotification", true);
            startActivity(intent);
            finish();
        } else {
            ReadAloudConnector.returnFromSettings = true;
            SetSettingsActivity.super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }

        super.onDestroy();
    }
}
