package com.rootekstudio.repeatsandroid.readaloud;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.activities.SetSettingsActivity;
import com.rootekstudio.repeatsandroid.database.RepeatsDatabase;
import com.rootekstudio.repeatsandroid.database.SetSingleItem;
import com.rootekstudio.repeatsandroid.database.SingleSetInfo;
import com.rootekstudio.repeatsandroid.mainpage.MainActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.rootekstudio.repeatsandroid.readaloud.ReadAloudConnector.readAloudService;

public class ReadAloudActivity extends AppCompatActivity {
    SingleSetInfo setInfo;
    TextView setName;
    ScrollView scrollView;
    LinearLayout linearButtons;
    LinearLayout linearReadingSpeed;
    Locale firstLocale;
    Locale secondLocale;

    List<SetSingleItem> singleSet;
    int allItems;

    boolean readAloudBound = false;
    boolean fromNotification;
    boolean speaking;

    public static Intent serviceIntent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        fromNotification = intent.getBooleanExtra("loadedFromNotification", false);

        setContentView(R.layout.activity_read_aloud);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        scrollView = findViewById(R.id.scrollViewReadAloud);
        linearButtons = findViewById(R.id.linearPlayButtonsRead);
        linearReadingSpeed = findViewById(R.id.readingSpeedLinear);
        setName = findViewById(R.id.setNameReadAloud);
        linearButtons.setVisibility(View.INVISIBLE);
        linearReadingSpeed.setVisibility(View.INVISIBLE);
        speaking = true;

        String locale0;
        String locale1;

        if (intent.getBooleanExtra("newReadAloud", false)) {
            ReadAloudConnector.reset();
            if (serviceIntent != null) {
                stopSpeakService();
            }

            ReadAloudConnector.setID = intent.getStringExtra("setID");
            RepeatsDatabase DB = RepeatsDatabase.getInstance(this);
            setInfo = DB.singleSetInfo(ReadAloudConnector.setID);
            singleSet = DB.allItemsInSet(ReadAloudConnector.setID, -1);

            setName.setText(setInfo.getSetName());
            locale0 = setInfo.getFirstLanguage();
            locale1 = setInfo.getSecondLanguage();

            ReadAloudConnector.locale0 = locale0;
            ReadAloudConnector.locale1 = locale1;
            ReadAloudConnector.singleSet = singleSet;
            ReadAloudConnector.setName = setInfo.getSetName();

            firstLocale = new Locale(locale0.substring(0, locale0.indexOf("_")), locale0.substring(locale0.indexOf("_") + 1));
            secondLocale = new Locale(locale1.substring(0, locale1.indexOf("_")), locale1.substring(locale1.indexOf("_") + 1));
        } else {
            singleSet = ReadAloudConnector.singleSet;
            locale0 = ReadAloudConnector.locale0;
            locale1 = ReadAloudConnector.locale1;
            setName.setText(ReadAloudConnector.setName);

            firstLocale = new Locale(locale0.substring(0, locale0.indexOf("_")), locale0.substring(locale0.indexOf("_") + 1));
            secondLocale = new Locale(locale1.substring(0, locale1.indexOf("_")), locale1.substring(locale1.indexOf("_") + 1));
        }
    }

    @Override
    protected void onStart() {
        ReadAloudConnector.isActivityAlive = true;
        if (fromNotification) {
            startPlayingShowButtons();
            if (ReadAloudConnector.speakItemIndex % 2 == 0) {
                speaking = false;
                updateLayout1();
                speaking = true;
            } else {
                speaking = false;
                updateLayout0();
                speaking = true;
            }
        }
        createAndBindService();

        super.onStart();
    }

    private void createAndBindService() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.rootekstudio.repeatsandroid");
        registerReceiver(broadcastReceiver, intentFilter);

        serviceIntent = new Intent(this, ReadAloudService.class);
        if (!fromNotification) {
            if (!ReadAloudConnector.returnFromSettings) {
                if (readAloudService == null) {
                    startService(serviceIntent);
                }
            } else {
                ReadAloudConnector.returnFromSettings = false;
            }
        }
        bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);
    }

    private void stopSpeakService() {
        stopService(serviceIntent);
        ReadAloudConnector.isTTSStopped = true;
        serviceIntent = null;
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getBooleanExtra("loadingDone", false)) {
                singleSet = ReadAloudConnector.singleSet;
                allItems = singleSet.size();
                startPlayingShowButtons();
                if (ReadAloudConnector.isTTSStopped) {
                    ReadAloudConnector.isTTSStopped = false;
                }
                updateLayout0();
            } else if (intent.getBooleanExtra("speakingDone0", false)) {
                updateLayout1();
            } else if (intent.getBooleanExtra("speakingDone1", false)) {
                updateLayout0();
            }
        }
    };

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            ReadAloudService.ReadAloudBinder binder = (ReadAloudService.ReadAloudBinder) service;
            readAloudService = binder.getService();

            SeekBar seekBar = findViewById(R.id.readingSpeedSeekBar);

            if (fromNotification) {
                singleSet = ReadAloudConnector.singleSet;
                allItems = singleSet.size();
                fromNotification = false;
                seekBar.setProgress(Math.round(ReadAloudConnector.speechRate * 100));
            }
            seekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);
            readAloudBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            readAloudBound = false;
        }
    };

    private void updateLayout0() {
        if (ReadAloudConnector.speakItemSetIndex == singleSet.size()) {
            ImageView playPause = findViewById(R.id.playPauseImageView);
            playPause.setImageResource(R.drawable.replay_24px);
            readAloudService.stopTextToSpeech();
            readAloudService.stopForegroundServ();
            return;
        }
        SetSingleItem singleSetDB = singleSet.get(ReadAloudConnector.speakItemSetIndex);
        String counter = ReadAloudConnector.speakItemSetIndex + 1 + "/" + ReadAloudConnector.singleSet.size();

        ArrayList<String> info = new ArrayList<>();
        info.add(firstLocale.getDisplayName());
        info.add(singleSetDB.getQuestion());
        info.add(secondLocale.getDisplayName());
        info.add(singleSetDB.getAnswer());
        info.add(counter);

        ReadAloudItemFragment readAloudItemFragment = new ReadAloudItemFragment();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("info", info);
        bundle.putInt("speakItemIndex", 0);
        readAloudItemFragment.setArguments(bundle);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.scrollViewReadAloud, readAloudItemFragment);
        fragmentTransaction.commit();

        if (speaking) {
            readAloudService.speak0();
        }

    }

    private void updateLayout1() {
        if (ReadAloudConnector.speakItemSetIndex == singleSet.size()) {
            ImageView playPause = findViewById(R.id.playPauseImageView);
            playPause.setImageResource(R.drawable.replay_24px);
            readAloudService.stopTextToSpeech();
            readAloudService.stopForegroundServ();
            return;
        }
        SetSingleItem singleSetDB = singleSet.get(ReadAloudConnector.speakItemSetIndex);
        String counter = ReadAloudConnector.speakItemSetIndex + 1 + "/" + ReadAloudConnector.singleSet.size();

        ArrayList<String> info = new ArrayList<>();
        info.add(firstLocale.getDisplayName());
        info.add(singleSetDB.getQuestion());
        info.add(secondLocale.getDisplayName());
        info.add(singleSetDB.getAnswer());
        info.add(counter);

        ReadAloudItemFragment readAloudItemFragment = new ReadAloudItemFragment();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("info", info);
        bundle.putInt("speakItemIndex", 1);
        readAloudItemFragment.setArguments(bundle);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.scrollViewReadAloud, readAloudItemFragment);
        fragmentTransaction.commit();

        if (speaking) {
            readAloudService.speak1();
        }
    }

    public void previousReadClick(View view) {
        if (!readAloudService.getTextToSpeech().isSpeaking() && !readAloudService.getTextToSpeech1().isSpeaking()) {
            speaking = false;
        } else {
            speaking = true;
        }
        readAloudService.stopTextToSpeech();

        if (ReadAloudConnector.speakItemSetIndex == singleSet.size() && !speaking) {
            ImageView playPause = findViewById(R.id.playPauseImageView);
            playPause.setImageResource(R.drawable.play_arrow_24px);
        }

        if (ReadAloudConnector.speakItemSetIndex == singleSet.size()) {
            if (singleSet.size() != 1) {
                ReadAloudConnector.speakItemSetIndex -= 2;
            }
        } else {
            if (ReadAloudConnector.speakItemSetIndex != 0) {
                ReadAloudConnector.speakItemSetIndex--;
            }
        }

        updateLayout0();
    }

    public void playPauseClick(View view) {
        ImageView imageView = (ImageView) view;

        if (ReadAloudConnector.speakItemSetIndex != singleSet.size()) {
            if (readAloudService.getTextToSpeech().isSpeaking() || readAloudService.getTextToSpeech1().isSpeaking()) {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                readAloudService.stopTextToSpeech();
                readAloudService.stopForegroundServ();
                imageView.setImageResource(R.drawable.play_arrow_24px);
            } else {
                readAloudService.resumeForeground();
                speaking = true;
                updateLayout0();
                imageView.setImageResource(R.drawable.pause_24px);
            }
        } else {
            ReadAloudConnector.speakItemSetIndex = 0;
            imageView.setImageResource(R.drawable.pause_24px);
            speaking = true;
            readAloudService.resumeForeground();
            updateLayout0();
        }
    }

    public void nextReadClick(View view) {
        if (!readAloudService.getTextToSpeech().isSpeaking() && !readAloudService.getTextToSpeech1().isSpeaking()) {
            speaking = false;
        } else {
            speaking = true;
        }
        readAloudService.stopTextToSpeech();

        if (ReadAloudConnector.speakItemSetIndex != singleSet.size()) {
            ReadAloudConnector.speakItemSetIndex++;
            updateLayout0();
        } else {
            ImageView playPause = findViewById(R.id.playPauseImageView);
            playPause.setImageResource(R.drawable.play_arrow_24px);
        }
    }

    SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            float speed = (float) i / 100;
            ReadAloudConnector.speechRate = speed;
            readAloudService.setSpeechRate();
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
        } else if (item.getItemId() == R.id.setSettingsInReadAloud) {
            Intent intent = new Intent(ReadAloudActivity.this, SetSettingsActivity.class);
            intent.putExtra("setID", ReadAloudConnector.setID);
            intent.putExtra("fromReadAloud", true);
            if (!ReadAloudConnector.isTTSStopped) {
                finish();
            }
            startActivity(intent);
        }
        return true;
    }

    private void startPlayingShowButtons() {
        scrollView.removeAllViews();
        linearButtons.setVisibility(View.VISIBLE);
        linearReadingSpeed.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPause() {
        ReadAloudConnector.isActivityAlive = false;
        unbindService(connection);
        unregisterReceiver(broadcastReceiver);

        super.onPause();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}