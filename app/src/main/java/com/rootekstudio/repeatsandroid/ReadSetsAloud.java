package com.rootekstudio.repeatsandroid;

import android.content.Context;
import android.content.DialogInterface;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.rootekstudio.repeatsandroid.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ReadSetsAloud {
    TextToSpeech textToSpeech;
    Context context;
    String setID;
    AlertDialog alertDialog;
    AutoCompleteTextView autoCompleteTextView;
    AutoCompleteTextView autoCompleteTextView1;

    public ReadSetsAloud(Context context, String setID) {
        this.context = context;
        this.setID = setID;
        initTTS();
    }

    private void initTTS() {
        textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                textToSpeech.setSpeechRate(0.7f);
            }
        });

        selectLanguages();
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

        View view = LayoutInflater.from(context).inflate(R.layout.select_language_tts, null);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.select_language_single_item, displayedlocaleList);
        autoCompleteTextView = view.findViewById(R.id.firstLanguageSelect);
        autoCompleteTextView1 = view.findViewById(R.id.secondLanguageSelect);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView1.setAdapter(adapter);

        Collections.sort(displayedlocaleList);

        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(context);
        alertDialogBuilder.setTitle(R.string.select_language);
        alertDialogBuilder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        alertDialogBuilder.setPositiveButton(R.string.start, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String locale0 = detailLocaleList.get(autoCompleteTextView.getText().toString());
                String locale1 = detailLocaleList.get(autoCompleteTextView1.getText().toString());

                if (Locale.getDefault().toString().equals("pl_PL")) {
                    if (locale0 == null || locale0.equals("")) {
                        locale0 = "pl_PL";
                    }

                    if (locale1 == null || locale1.equals("")) {
                        locale1 = "en_GB";
                    }
                } else {
                    if (locale0 == null || locale0.equals("")) {
                        locale0 = "en_US";
                    }

                    if (locale1 == null || locale1.equals("")) {
                        locale1 = "fr_FR";
                    }
                }

                alertDialog.dismiss();
                readSetAloud(locale0, locale1);
            }
        });
        alertDialogBuilder.setView(view);
        alertDialog = alertDialogBuilder.show();

    }

    private void readSetAloud(final String locale0, final String locale1) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DatabaseHelper DB = new DatabaseHelper(context);
                List<RepeatsSingleSetDB> itemsSetList = DB.AllItemsSET(setID);
                for (int i = 0; i < itemsSetList.size(); i++) {
                    final RepeatsSingleSetDB item = itemsSetList.get(i);

                    textToSpeech.setLanguage(new Locale(locale0));
                    textToSpeech.speak(item.getQuestion(), TextToSpeech.QUEUE_ADD, null, "");
                    textToSpeech.setLanguage(new Locale(locale1));
                    textToSpeech.speak(item.getAnswer(), TextToSpeech.QUEUE_ADD, null, "");
                }

            }
        }).start();
    }

    public TextToSpeech getTextToSpeech() {
        return textToSpeech;
    }
}
