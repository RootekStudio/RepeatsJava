package com.rootekstudio.repeatsandroid.activities;

import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.text.style.UpdateLayout;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.ReadAloudItemFragment;
import com.rootekstudio.repeatsandroid.ReadSetsAloud;
import com.rootekstudio.repeatsandroid.RepeatsListDB;
import com.rootekstudio.repeatsandroid.RepeatsSingleSetDB;
import com.rootekstudio.repeatsandroid.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ReadAloudActivity extends AppCompatActivity {
    RepeatsListDB setInfo;
    List<RepeatsSingleSetDB> singleSet;
    TextView setName;
    ScrollView scrollView;
    TextView firstWord;
    TextView secondWord;

    Locale firstLocale;
    Locale secondLocale;

    String locale0;
    String locale1;

    int allItems;
    int speakItemIndex;
    int speakItemSetIndex;

    TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_aloud);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String setID = intent.getStringExtra("setID");
        DatabaseHelper DB = new DatabaseHelper(this);
        singleSet = DB.AllItemsSET(setID);
        allItems = singleSet.size();
        setInfo = DB.getSingleItemLIST(setID);
        scrollView = findViewById(R.id.scrollViewReadAloud);
        speakItemIndex = 0;
        speakItemSetIndex = 0;

        setName = findViewById(R.id.setNameReadAloud);

        setName.setText(setInfo.getitle());
        locale0 = setInfo.getFirstLanguage();
        locale1 = setInfo.getSecondLanguage();

        firstLocale = new Locale(locale0.substring(0, locale0.indexOf("_")), locale0.substring(locale0.indexOf("_") + 1));
        secondLocale = new Locale(locale1.substring(0, locale1.indexOf("_")), locale1.substring(locale1.indexOf("_") + 1));

        SeekBar seekBar = findViewById(R.id.readingSpeedSeekBar);
        seekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);

        initTTS();
    }

    private void initTTS() {
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                textToSpeech.setOnUtteranceProgressListener(utteranceProgressListener);
                textToSpeech.setSpeechRate(0.5f);
                updateLayout(true);
            }
        });
    }

    UtteranceProgressListener utteranceProgressListener = new UtteranceProgressListener() {
        @Override
        public void onStart(String s) {
        }

        @Override
        public void onDone(String s) {
            updateLayout(true);
                if (speakItemIndex % 2 == 0) {
                    speakItemSetIndex++;
                }
        }

        @Override
        public void onError(String s) {

        }
    };

    private void updateLayout(boolean speaking) {
        if(speakItemSetIndex != singleSet.size()) {
            RepeatsSingleSetDB singleSetDB = singleSet.get(speakItemSetIndex);
            String counter = speakItemSetIndex + 1 + "/" + allItems;

            ArrayList<String> info = new ArrayList<>();
            info.add(firstLocale.getDisplayName());
            info.add(singleSetDB.getQuestion());
            info.add(secondLocale.getDisplayName());
            info.add(singleSetDB.getAnswer());
            info.add(counter);

            ReadAloudItemFragment readAloudItemFragment = new ReadAloudItemFragment();
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("info", info);
            bundle.putInt("speakItemIndex", speakItemIndex);
            readAloudItemFragment.setArguments(bundle);

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.scrollViewReadAloud, readAloudItemFragment);
            fragmentTransaction.commit();

            if(speaking) {
                if(speakItemIndex%2 == 0) {
                    textToSpeech.setLanguage(new Locale(locale0));
                    textToSpeech.speak(singleSetDB.getQuestion(), TextToSpeech.QUEUE_ADD, null, "");
                }
                else {
                    textToSpeech.setLanguage(new Locale(locale1));
                    textToSpeech.speak(singleSetDB.getAnswer(), TextToSpeech.QUEUE_ADD, null, "");
                }

                speakItemIndex++;

            }
        }
        else {
            ImageView playPause = findViewById(R.id.playPauseImageView);
            playPause.setImageResource(R.drawable.replay_24px);
        }
    }

    public void previousReadClick(View view) {
        boolean wasSpeaking = true;
        if(!textToSpeech.isSpeaking()) {
            wasSpeaking = false;
        }
        textToSpeech.stop();

        if (speakItemSetIndex != 0) {
            speakItemSetIndex--;
        }
        speakItemIndex = 0;
        updateLayout(wasSpeaking);
    }

    public void playPauseClick(View view) {
        ImageView imageView = (ImageView) view;

        if(speakItemSetIndex != singleSet.size()+1) {
            if (textToSpeech.isSpeaking()) {
                textToSpeech.stop();
                if(speakItemIndex%2 == 0) {
                    speakItemSetIndex--;
                }
                speakItemIndex = 0;
                imageView.setImageResource(R.drawable.play_arrow_24px);
            } else {
                updateLayout(true);
                imageView.setImageResource(R.drawable.pause_24px);
            }
        }
        else {
            speakItemSetIndex = 0;
            updateLayout(true);
            imageView.setImageResource(R.drawable.pause_24px);
        }

    }

    public void nextReadClick(View view) {
        boolean wasSpeaking = true;
        if(!textToSpeech.isSpeaking()) {
            wasSpeaking = false;
        }
        textToSpeech.stop();
        if (speakItemSetIndex != singleSet.size()) {
            speakItemSetIndex++;
        }
        speakItemIndex = 0;

        updateLayout(wasSpeaking);
    }

    SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            float speed = (float)i/100;
            textToSpeech.setSpeechRate(speed);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }
}
