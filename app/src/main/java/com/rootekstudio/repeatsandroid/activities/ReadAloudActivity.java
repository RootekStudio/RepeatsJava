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
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
    String setID;
    ScrollView scrollView;

    Locale firstLocale;
    Locale secondLocale;

    String locale0;
    String locale1;

    int allItems;
    int speakItemIndex;
    int speakItemSetIndex;

    TextToSpeech textToSpeech;
    TextToSpeech textToSpeech1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_aloud);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        scrollView = findViewById(R.id.scrollViewReadAloud);
        Intent intent = getIntent();
        setID = intent.getStringExtra("setID");
        DatabaseHelper DB = new DatabaseHelper(this);
        singleSet = DB.AllItemsSET(setID);
        allItems = singleSet.size();
        setInfo = DB.getSingleItemLIST(setID);

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
                textToSpeech.setLanguage(new Locale(locale0));
                textToSpeech.setOnUtteranceProgressListener(utteranceProgressListener);
                textToSpeech.setSpeechRate(0.5f);

                textToSpeech1 = new TextToSpeech(ReadAloudActivity.this, new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int i) {
                        textToSpeech1.setLanguage(new Locale(locale1));
                        textToSpeech1.setOnUtteranceProgressListener(utteranceProgressListener1);
                        textToSpeech1.setSpeechRate(0.5f);

                        textToSpeech.speak("", TextToSpeech.QUEUE_ADD, null, "0");
                        textToSpeech1.speak("", TextToSpeech.QUEUE_ADD, null, "0");
                    }
                });
            }
        });
    }

    UtteranceProgressListener utteranceProgressListener = new UtteranceProgressListener() {
        @Override
        public void onStart(String s) {
        }

        @Override
        public void onDone(String s) {
            if(s.equals("0")){
                return;
            }
            speakItemIndex++;
            updateLayout(true);
        }

        @Override
        public void onError(String s) {
        }
    };

    UtteranceProgressListener utteranceProgressListener1 = new UtteranceProgressListener() {
        @Override
        public void onStart(String s) {
        }

        @Override
        public void onDone(String s) {
            if(s.equals("0")){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.removeAllViews();
                        updateLayout(true);
                    }
                });

                return;
            }

            if (speakItemSetIndex != singleSet.size() - 1) {
                speakItemIndex++;
                speakItemSetIndex++;
                updateLayout(true);
            } else {
                ImageView playPause = findViewById(R.id.playPauseImageView);
                playPause.setImageResource(R.drawable.replay_24px);
            }
        }

        @Override
        public void onError(String s) {
        }
    };

    private void updateLayout(boolean speaking) {
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

        if (speaking) {
            if (speakItemIndex % 2 == 0) {
                textToSpeech.speak(singleSetDB.getQuestion(), TextToSpeech.QUEUE_ADD, null, "");
            } else {
                textToSpeech1.speak(singleSetDB.getAnswer(), TextToSpeech.QUEUE_ADD, null, "");
            }
        }
    }

    public void previousReadClick(View view) {
        boolean wasSpeaking = true;
        if (!textToSpeech.isSpeaking()) {
            wasSpeaking = false;
        }
        textToSpeech.stop();
        textToSpeech1.stop();

        if (speakItemSetIndex == singleSet.size() - 1 && !wasSpeaking) {
            ImageView playPause = findViewById(R.id.playPauseImageView);
            playPause.setImageResource(R.drawable.play_arrow_24px);
        }

        if (speakItemSetIndex != 0) {
            speakItemSetIndex--;
        }

        speakItemIndex = 0;
        updateLayout(wasSpeaking);
    }

    public void playPauseClick(View view) {
        ImageView imageView = (ImageView) view;

        if (speakItemSetIndex != singleSet.size() - 1) {
            if (textToSpeech.isSpeaking() || textToSpeech1.isSpeaking()) {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                textToSpeech.stop();
                textToSpeech1.stop();
                speakItemIndex = 0;
                imageView.setImageResource(R.drawable.play_arrow_24px);
            } else {
                updateLayout(true);
                imageView.setImageResource(R.drawable.pause_24px);
            }
        } else {
            if(textToSpeech.isSpeaking() || textToSpeech1.isSpeaking()) {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                textToSpeech.stop();
                textToSpeech1.stop();
                imageView.setImageResource(R.drawable.play_arrow_24px);
            }
            else {
                if(speakItemIndex%2 != 0) {
                    speakItemIndex = 0;
                    speakItemSetIndex = 0;
                    imageView.setImageResource(R.drawable.pause_24px);
                }
                else {
                    imageView.setImageResource(R.drawable.pause_24px);
                }
                updateLayout(true);
            }

        }

    }

    public void nextReadClick(View view) {
        boolean wasSpeaking = true;
        if (!textToSpeech.isSpeaking() || !textToSpeech1.isSpeaking()) {
            wasSpeaking = false;
        }
        textToSpeech.stop();
        textToSpeech1.stop();
        if (speakItemSetIndex != singleSet.size() - 1) {
            speakItemSetIndex++;
        }
        speakItemIndex = 0;

        updateLayout(wasSpeaking);
    }

    SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            float speed = (float) i / 100;
            textToSpeech.setSpeechRate(speed);
            textToSpeech1.setSpeechRate(speed);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.read_aloud_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        else if(item.getItemId() == R.id.setSettingsInReadAloud) {
            Intent intent = new Intent(ReadAloudActivity.this, SetSettingsActivity.class);
            intent.putExtra("setID", setID);
            startActivity(intent);
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        if(textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }

        if(textToSpeech1 != null) {
            textToSpeech1.stop();
            textToSpeech1.shutdown();
        }
        super.onDestroy();
    }
}
