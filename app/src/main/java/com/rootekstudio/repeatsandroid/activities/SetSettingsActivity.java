package com.rootekstudio.repeatsandroid.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.database.DatabaseHelper;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        setID = intent.getStringExtra("setID");
        DB = new DatabaseHelper(this);
        initTTS();
    }

    private void initTTS() {
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                selectLanguages();
            }
        });
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

        String defaults = "Język pytań: " + DB.getValue("firstLanguage", "TitleTable", "TableName='" + setID + "'") + "\n"
                + "Język odpowiedzi: " + DB.getValue("secondLanguage", "TitleTable", "TableName='" + setID + "'");

        TextView defaultsLanguages = findViewById(R.id.defaultsLanguageSettings);
        defaultsLanguages.setText(defaults);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.select_language_single_item, displayedlocaleList);
        autoCompleteTextView = findViewById(R.id.firstLanguageSelect);
        autoCompleteTextView1 = findViewById(R.id.secondLanguageSelect);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView autoCompleteTextView = (TextView) view;
                String localeString = detailLocaleList.get(autoCompleteTextView.getText().toString());
                if (localeString != null) {
                    DB.UpdateTable("TitleTable", "firstLanguage='" + localeString + "'", "TableName='" + setID + "'");
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
                }
            }
        });
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView1.setAdapter(adapter);

        Collections.sort(displayedlocaleList);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }
}
